package com.vetnova.ventas.service;

import com.vetnova.ventas.dto.ConsumoInventarioRequest;
import com.vetnova.ventas.dto.MovimientoInventarioRequest;
import com.vetnova.ventas.event.EventoDominio;
import com.vetnova.ventas.exception.BusinessException;
import com.vetnova.ventas.exception.ResourceNotFoundException;
import com.vetnova.ventas.model.Venta;
import com.vetnova.ventas.repository.VentaRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.client.RestClientException;

@Slf4j
@Service
@Transactional
public class VentaService {

    private final VentaRepository ventaRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final RestTemplate restTemplate;

    @Value("${inventario.url}")
    private String inventarioUrl;

    @Value("${services.clientes.url:http://localhost:8084}")
    private String clientesServiceUrl;

    @Value("${services.inventario.url:http://localhost:8087}")
    private String inventarioServiceUrl;

    @Value("${services.sucursales.url:http://localhost:8090}")
    private String sucursalesServiceUrl;

    @Value("${notificaciones.url:http://localhost:8089/api/v1/notificaciones}")
    private String notificacionesUrl;

    public VentaService(VentaRepository ventaRepository,
                        ApplicationEventPublisher eventPublisher,
                        RestTemplate restTemplate) {
        this.ventaRepository = ventaRepository;
        this.eventPublisher = eventPublisher;
        this.restTemplate = restTemplate;
    }

    public Venta registrarVenta(Venta venta) {
        log.info("Registrando nueva venta ID de producto: {}", venta.getIdProducto());
        validarClienteExistente(venta.getIdCliente());
        validarDisponibilidadProducto(venta.getIdProducto(), venta.getCantidad());
        validarSucursalExistente(venta.getIdSucursal());
        venta.setEstado("PENDIENTE");
        venta.setFechaVenta(LocalDateTime.now());
        return ventaRepository.save(venta);
    }

    public Venta procesarPago(Long id) {
        Venta venta = obtenerVentaPorId(id);
        if ("PAGADA".equals(venta.getEstado())) {
            throw new BusinessException("La venta ya se encuentra PAGADA.");
        }
        venta.setEstado("PAGADA");
        Venta ventaPagada = ventaRepository.save(venta);

        Map<String, Object> payloadPago = new HashMap<>();
        payloadPago.put("idVenta", ventaPagada.getId());
        payloadPago.put("monto", ventaPagada.getMontoTotal());

        EventoDominio<Map<String, Object>> eventoPago = new EventoDominio<>(
                "PagoConfirmado", "ms-ventas", payloadPago
        );

        eventPublisher.publishEvent(eventoPago);

        Map<String, Object> payloadVenta = new HashMap<>();
        payloadVenta.put("idVenta", ventaPagada.getId());
        payloadVenta.put("idProducto", ventaPagada.getIdProducto());
        payloadVenta.put("cantidad", ventaPagada.getCantidad());

        EventoDominio<Map<String, Object>> eventoVenta = new EventoDominio<>(
                "VentaConfirmada", "ms-ventas", payloadVenta
        );

        eventPublisher.publishEvent(eventoVenta);

        enviarConsumoInventario(ventaPagada);
        notificarVentaPagada(ventaPagada);

        log.info("Venta pagada, eventos emitidos e inventario actualizado.");

        return ventaPagada;
    }

    public Venta registrarDevolucion(Long id) {
        Venta venta = obtenerVentaPorId(id);
        if (!"PAGADA".equals(venta.getEstado())) {
            throw new BusinessException("Solo se pueden devolver ventas en estado PAGADA.");
        }
        venta.setEstado("DEVUELTA");
        Venta ventaDevuelta = ventaRepository.save(venta);

        Map<String, Object> payloadDev = new HashMap<>();
        payloadDev.put("idVenta", ventaDevuelta.getId());
        payloadDev.put("idProducto", ventaDevuelta.getIdProducto());
        payloadDev.put("cantidad", ventaDevuelta.getCantidad());

        EventoDominio<Map<String, Object>> eventoDev = new EventoDominio<>(
                "DevolucionRegistrada", "ms-ventas", payloadDev
        );

        eventPublisher.publishEvent(eventoDev);

        enviarMovimientoEntradaInventario(ventaDevuelta);

        log.info("Devolución registrada, evento emitido e inventario actualizado.");

        return ventaDevuelta;
    }

    private void enviarConsumoInventario(Venta venta) {
        try {
            ConsumoInventarioRequest request = new ConsumoInventarioRequest(
                    venta.getIdProducto(),
                    venta.getIdSucursal(),
                    venta.getCantidad(),
                    "VENTA",
                    venta.getId(),
                    "Venta Nº" + venta.getId()
            );

            restTemplate.postForEntity(inventarioServiceUrl + "/api/v1/inventario/consumos", request, Object.class);

            log.info("Consumo de inventario registrado para producto ID: {}", venta.getIdProducto());

        } catch (RestClientException e) {
            log.warn("La venta fue pagada, pero no se pudo registrar el consumo en inventario: {}", e.getMessage());
        }
    }

    private void notificarVentaPagada(Venta venta) {
        try {
            Map<String, Object> request = new HashMap<>();
            request.put("destino", "CLIENTE");
            request.put("idCliente", venta.getIdCliente());
            request.put("mensaje", "Tu compra fue procesada correctamente. Venta Nº" + venta.getId());
            request.put("tipo", "VENTA");
            request.put("canal", "EMAIL");
            request.put("prioridad", "MEDIA");

            restTemplate.postForEntity(notificacionesUrl, request, Object.class);

            log.info("Notificación de venta pagada enviada para venta ID: {}", venta.getId());

        } catch (RestClientException e) {
            log.warn("La venta fue pagada, pero no se pudo notificar al cliente: {}", e.getMessage());
        }
    }

    private void enviarMovimientoEntradaInventario(Venta venta) {
        try {
            MovimientoInventarioRequest request = new MovimientoInventarioRequest(
                    venta.getIdProducto(),
                    venta.getIdSucursal(),
                    "ENTRADA",
                    venta.getCantidad(),
                    "Entrada automática por devolución de venta ID: " + venta.getId()
            );

            restTemplate.postForEntity(inventarioUrl, request, String.class);

            log.info("Movimiento de entrada enviado a Inventario para producto ID: {}", venta.getIdProducto());

        } catch (RestClientException e) {
            log.warn("La devolución fue registrada, pero no se pudo devolver stock a inventario: {}", e.getMessage());
        }
    }

    public Venta emitirBoleta(Long id) {
        Venta venta = obtenerVentaPorId(id);

        if (!venta.getEstado().equals("PAGADA")) {
            throw new RuntimeException("Solo se emiten boletas de ventas pagadas.");
        }

        return venta;
    }

    public List<Venta> obtenerTodasLasVentas() {
        return ventaRepository.findAll();
    }

    public Venta obtenerVentaPorId(Long id) {
        return ventaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada con ID: " + id));
    }

    private void validarClienteExistente(Long idCliente) {
        String url = clientesServiceUrl + "/api/v1/clientes/" + idCliente;
        try {
            restTemplate.getForEntity(url, Object.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ResourceNotFoundException("El cliente con ID " + idCliente + " no existe.");
            }
            throw new ResourceNotFoundException("No se pudo validar el cliente con ID " + idCliente + ".");
        } catch (ResourceAccessException e) {
            throw new com.vetnova.ventas.exception.ServiceUnavailableException("El microservicio de Clientes no se encuentra activo. Operación abortada por integridad.");
        }
    }

    @SuppressWarnings("unchecked")
    private void validarDisponibilidadProducto(Long idProducto, Integer cantidad) {
        String url = inventarioServiceUrl + "/api/v1/inventario/disponibilidad/" + idProducto + "?cantidad=" + cantidad;
        try {
            var response = restTemplate.getForEntity(url, Map.class);
            Map<String, Object> body = response.getBody();

            boolean disponible = body != null && Boolean.TRUE.equals(body.get("disponible"));
            boolean activo = body != null && Boolean.TRUE.equals(body.get("activo"));

            if (!disponible || !activo) {
                String mensaje = body != null && body.get("mensaje") != null
                        ? body.get("mensaje").toString()
                        : "El producto con ID " + idProducto + " no está disponible.";
                throw new BusinessException(mensaje);
            }
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ResourceNotFoundException("El producto con ID " + idProducto + " no existe.");
            }
            throw new ResourceNotFoundException("No se pudo validar el producto con ID " + idProducto + ".");
        } catch (ResourceAccessException e) {
            throw new com.vetnova.ventas.exception.ServiceUnavailableException("El microservicio de Inventario no se encuentra activo. Operación abortada por integridad.");
        }
    }

    private void validarSucursalExistente(Long idSucursal) {
        String url = sucursalesServiceUrl + "/api/v1/sucursales/" + idSucursal + "/validar";
        try {
            restTemplate.getForEntity(url, Object.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ResourceNotFoundException("La sucursal con ID " + idSucursal + " no existe.");
            }
            throw new ResourceNotFoundException("No se pudo validar la sucursal con ID " + idSucursal + ".");
        } catch (ResourceAccessException e) {
            throw new com.vetnova.ventas.exception.ServiceUnavailableException("El microservicio de Sucursales no se encuentra activo. Operación abortada por integridad.");
        }
    }
}