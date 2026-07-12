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
}