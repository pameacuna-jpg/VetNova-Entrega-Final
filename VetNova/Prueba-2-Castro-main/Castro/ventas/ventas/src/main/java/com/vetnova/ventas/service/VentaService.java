package com.vetnova.ventas.service;

import com.vetnova.ventas.dto.MovimientoInventarioRequest;
import com.vetnova.ventas.event.EventoDominio;
import com.vetnova.ventas.exception.ResourceNotFoundException;
import com.vetnova.ventas.model.Venta;
import com.vetnova.ventas.repository.VentaRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Transactional
public class VentaService {

    private final VentaRepository ventaRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final RestTemplate restTemplate;

    @Value("${inventario.url}")
    private String inventarioUrl;

    public VentaService(VentaRepository ventaRepository,
                        ApplicationEventPublisher eventPublisher,
                        RestTemplate restTemplate) {
        this.ventaRepository = ventaRepository;
        this.eventPublisher = eventPublisher;
        this.restTemplate = restTemplate;
    }

    public Venta registrarVenta(Venta venta) {
        log.info("Registrando nueva venta ID de producto: {}", venta.getIdProducto());
        venta.setEstado("PENDIENTE");
        venta.setFechaVenta(LocalDateTime.now());
        return ventaRepository.save(venta);
    }

    public Venta procesarPago(Long id) {
        Venta venta = obtenerVentaPorId(id);
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

        enviarMovimientoSalidaInventario(ventaPagada);

        log.info("Venta pagada, eventos emitidos e inventario actualizado.");

        return ventaPagada;
    }

    public Venta registrarDevolucion(Long id) {
        Venta venta = obtenerVentaPorId(id);
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

    private void enviarMovimientoSalidaInventario(Venta venta) {
        try {
            MovimientoInventarioRequest request = new MovimientoInventarioRequest(
                    venta.getIdProducto(),
                    1L,
                    "SALIDA",
                    venta.getCantidad(),
                    "Salida automática por venta pagada ID: " + venta.getId()
            );

            restTemplate.postForEntity(inventarioUrl, request, String.class);

            log.info("Movimiento de salida enviado a Inventario para producto ID: {}", venta.getIdProducto());

        } catch (Exception e) {
            log.warn("La venta fue pagada, pero no se pudo descontar inventario: {}", e.getMessage());
        }
    }

    private void enviarMovimientoEntradaInventario(Venta venta) {
        try {
            MovimientoInventarioRequest request = new MovimientoInventarioRequest(
                    venta.getIdProducto(),
                    1L,
                    "ENTRADA",
                    venta.getCantidad(),
                    "Entrada automática por devolución de venta ID: " + venta.getId()
            );

            restTemplate.postForEntity(inventarioUrl, request, String.class);

            log.info("Movimiento de entrada enviado a Inventario para producto ID: {}", venta.getIdProducto());

        } catch (Exception e) {
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
}