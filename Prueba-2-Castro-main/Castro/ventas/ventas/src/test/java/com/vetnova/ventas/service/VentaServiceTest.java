package com.vetnova.ventas.service;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.vetnova.ventas.event.EventoDominio;
import com.vetnova.ventas.exception.BusinessException;
import com.vetnova.ventas.exception.ResourceNotFoundException;
import com.vetnova.ventas.exception.ServiceUnavailableException;
import com.vetnova.ventas.model.Venta;
import com.vetnova.ventas.repository.VentaRepository;

@ExtendWith(MockitoExtension.class)
public class VentaServiceTest {

    @Mock
    private VentaRepository ventaRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private VentaService ventaService;

    private Venta venta;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(ventaService, "clientesServiceUrl", "http://localhost:8084");
        ReflectionTestUtils.setField(ventaService, "inventarioServiceUrl", "http://localhost:8087");
        ReflectionTestUtils.setField(ventaService, "sucursalesServiceUrl", "http://localhost:8090");
        ReflectionTestUtils.setField(ventaService, "notificacionesUrl", "http://localhost:8089/api/v1/notificaciones");
        ReflectionTestUtils.setField(ventaService, "inventarioUrl", "http://localhost:8087/api/v1/movimientos");

        venta = new Venta();
        venta.setId(1L);
        venta.setIdCliente(10L);
        venta.setIdProducto(100L);
        venta.setIdSucursal(1L);
        venta.setCantidad(2);
        venta.setMontoTotal(50000.0);
        venta.setEstado("PENDIENTE");
    }

    @Test
    void testRegistrarVenta_Exito() {
        // Mock validaciones
        when(restTemplate.getForEntity(contains("/clientes/"), eq(Object.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
        when(restTemplate.getForEntity(contains("/disponibilidad/"), eq(Map.class)))
                .thenReturn(new ResponseEntity<>(Map.of("disponible", true, "activo", true), HttpStatus.OK));
        when(restTemplate.getForEntity(contains("/sucursales/"), eq(Object.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        when(ventaRepository.save(any(Venta.class))).thenReturn(venta);

        Venta resultado = ventaService.registrarVenta(venta);

        assertNotNull(resultado);
        verify(ventaRepository).save(any(Venta.class));
    }

    @Test
    void registrarVenta_siLaSucursalNoExiste_debeLanzarResourceNotFoundException() {
        // 1. Mock Cliente OK
        when(restTemplate.getForEntity(contains("/clientes/"), eq(Object.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
        // 2. Mock Producto OK
        when(restTemplate.getForEntity(contains("/disponibilidad/"), eq(Map.class)))
                .thenReturn(new ResponseEntity<>(Map.of("disponible", true, "activo", true), HttpStatus.OK));
        // 3. Mock Sucursal Falla
        when(restTemplate.getForEntity(contains("/sucursales/"), eq(Object.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        assertThrows(ResourceNotFoundException.class, () -> ventaService.registrarVenta(venta));
        verify(ventaRepository, never()).save(any(Venta.class));
    }

    @Test
    void procesarPago_ExitoYEventos() {
        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));
        when(ventaRepository.save(any(Venta.class))).thenReturn(venta);

        ventaService.procesarPago(1L);

        verify(eventPublisher, times(2)).publishEvent(any(EventoDominio.class));
        assertEquals("PAGADA", venta.getEstado());
    }

    @Test
    void registrarVenta_siElServicioClientesEstaCaido_debeLanzarServiceUnavailableException() {
        when(restTemplate.getForEntity(contains("/clientes/"), eq(Object.class)))
                .thenThrow(new ResourceAccessException("Service down"));

        assertThrows(ServiceUnavailableException.class, () -> ventaService.registrarVenta(venta));
    }

    // =========================================================================
    // TESTS NUEVOS
    // =========================================================================

    // ---- validarDisponibilidadProducto: producto no disponible/activo ----
    @Test
    void registrarVenta_siProductoNoDisponible_debeLanzarBusinessException() {
        when(restTemplate.getForEntity(contains("/clientes/"), eq(Object.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
        when(restTemplate.getForEntity(contains("/disponibilidad/"), eq(Map.class)))
                .thenReturn(new ResponseEntity<>(Map.of("disponible", false, "activo", true, "mensaje", "Sin stock"), HttpStatus.OK));

        BusinessException ex = assertThrows(BusinessException.class, () -> ventaService.registrarVenta(venta));
        assertEquals("Sin stock", ex.getMessage());
        verify(ventaRepository, never()).save(any(Venta.class));
    }

    // ---- validarDisponibilidadProducto: 404 ----
    @Test
    void registrarVenta_siElProductoNoExiste_debeLanzarResourceNotFoundException() {
        when(restTemplate.getForEntity(contains("/clientes/"), eq(Object.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
        when(restTemplate.getForEntity(contains("/disponibilidad/"), eq(Map.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        assertThrows(ResourceNotFoundException.class, () -> ventaService.registrarVenta(venta));
    }

    // ---- validarDisponibilidadProducto: error distinto a 404 ----
    @Test
    void registrarVenta_siInventarioResponde500_debeLanzarResourceNotFoundException() {
        when(restTemplate.getForEntity(contains("/clientes/"), eq(Object.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
        when(restTemplate.getForEntity(contains("/disponibilidad/"), eq(Map.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        assertThrows(ResourceNotFoundException.class, () -> ventaService.registrarVenta(venta));
    }

    // ---- validarDisponibilidadProducto: inventario caído (ResourceAccessException) ----
    @Test
    void registrarVenta_siInventarioEstaCaido_debeLanzarServiceUnavailableException() {
        when(restTemplate.getForEntity(contains("/clientes/"), eq(Object.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
        when(restTemplate.getForEntity(contains("/disponibilidad/"), eq(Map.class)))
                .thenThrow(new ResourceAccessException("Service down"));

        assertThrows(ServiceUnavailableException.class, () -> ventaService.registrarVenta(venta));
    }

    // ---- validarSucursalExistente: error distinto a 404 ----
    @Test
    void registrarVenta_siSucursalResponde500_debeLanzarResourceNotFoundException() {
        when(restTemplate.getForEntity(contains("/clientes/"), eq(Object.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
        when(restTemplate.getForEntity(contains("/disponibilidad/"), eq(Map.class)))
                .thenReturn(new ResponseEntity<>(Map.of("disponible", true, "activo", true), HttpStatus.OK));
        when(restTemplate.getForEntity(contains("/sucursales/"), eq(Object.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        assertThrows(ResourceNotFoundException.class, () -> ventaService.registrarVenta(venta));
    }

    // ---- validarSucursalExistente: caída (ResourceAccessException) ----
    @Test
    void registrarVenta_siSucursalesEstaCaido_debeLanzarServiceUnavailableException() {
        when(restTemplate.getForEntity(contains("/clientes/"), eq(Object.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
        when(restTemplate.getForEntity(contains("/disponibilidad/"), eq(Map.class)))
                .thenReturn(new ResponseEntity<>(Map.of("disponible", true, "activo", true), HttpStatus.OK));
        when(restTemplate.getForEntity(contains("/sucursales/"), eq(Object.class)))
                .thenThrow(new ResourceAccessException("Service down"));

        assertThrows(ServiceUnavailableException.class, () -> ventaService.registrarVenta(venta));
    }

    // ---- procesarPago: venta ya pagada ----
    @Test
    void procesarPago_siVentaYaPagada_debeLanzarBusinessException() {
        venta.setEstado("PAGADA");
        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));

        assertThrows(BusinessException.class, () -> ventaService.procesarPago(1L));
        verify(ventaRepository, never()).save(any(Venta.class));
    }

    // ---- procesarPago: venta no encontrada ----
    @Test
    void procesarPago_siVentaNoExiste_debeLanzarResourceNotFoundException() {
        when(ventaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> ventaService.procesarPago(99L));
    }

    // ---- procesarPago: falla el llamado a Inventario/Notificaciones (catch RestClientException) ----
    @Test
    void procesarPago_siFallaInventarioYNotificaciones_debeSeguirRetornandoVentaPagada() {
        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));
        when(ventaRepository.save(any(Venta.class))).thenReturn(venta);
        when(restTemplate.postForEntity(contains("/consumos"), any(), eq(Object.class)))
                .thenThrow(new ResourceAccessException("timeout"));
        when(restTemplate.postForEntity(contains("/notificaciones"), any(), eq(Object.class)))
                .thenThrow(new ResourceAccessException("timeout"));

        Venta resultado = ventaService.procesarPago(1L);

        assertEquals("PAGADA", resultado.getEstado());
    }

    // ---- registrarDevolucion: éxito ----
    @Test
    void registrarDevolucion_Exito() {
        venta.setEstado("PAGADA");
        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));
        when(ventaRepository.save(any(Venta.class))).thenReturn(venta);

        Venta resultado = ventaService.registrarDevolucion(1L);

        assertEquals("DEVUELTA", resultado.getEstado());
        verify(eventPublisher).publishEvent(any(EventoDominio.class));
    }

    // ---- registrarDevolucion: venta no está PAGADA ----
    @Test
    void registrarDevolucion_siVentaNoEstaPagada_debeLanzarBusinessException() {
        venta.setEstado("PENDIENTE");
        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));

        assertThrows(BusinessException.class, () -> ventaService.registrarDevolucion(1L));
        verify(ventaRepository, never()).save(any(Venta.class));
    }

    // ---- registrarDevolucion: venta no encontrada ----
    @Test
    void registrarDevolucion_siVentaNoExiste_debeLanzarResourceNotFoundException() {
        when(ventaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> ventaService.registrarDevolucion(99L));
    }

    // ---- registrarDevolucion: falla reingreso a inventario (catch RestClientException) ----
    @Test
    void registrarDevolucion_siFallaInventario_debeSeguirRetornandoVentaDevuelta() {
        venta.setEstado("PAGADA");
        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));
        when(ventaRepository.save(any(Venta.class))).thenReturn(venta);
        when(restTemplate.postForEntity(eq("http://localhost:8087/api/v1/movimientos"), any(), eq(String.class)))
                .thenThrow(new ResourceAccessException("timeout"));

        Venta resultado = ventaService.registrarDevolucion(1L);

        assertEquals("DEVUELTA", resultado.getEstado());
    }

    // ---- emitirBoleta: éxito ----
    @Test
    void emitirBoleta_siVentaPagada_debeRetornarVenta() {
        venta.setEstado("PAGADA");
        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));

        Venta resultado = ventaService.emitirBoleta(1L);

        assertEquals("PAGADA", resultado.getEstado());
    }

    // ---- emitirBoleta: venta no pagada ----
    @Test
    void emitirBoleta_siVentaNoPagada_debeLanzarRuntimeException() {
        venta.setEstado("PENDIENTE");
        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));

        assertThrows(RuntimeException.class, () -> ventaService.emitirBoleta(1L));
    }

    // ---- obtenerTodasLasVentas ----
    @Test
    void obtenerTodasLasVentas_debeRetornarListaDelRepositorio() {
        when(ventaRepository.findAll()).thenReturn(java.util.List.of(venta));

        java.util.List<Venta> resultado = ventaService.obtenerTodasLasVentas();

        assertEquals(1, resultado.size());
        verify(ventaRepository).findAll();
    }
}
