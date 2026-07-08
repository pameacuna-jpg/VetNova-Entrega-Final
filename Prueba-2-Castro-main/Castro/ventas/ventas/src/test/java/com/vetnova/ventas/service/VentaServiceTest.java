package com.vetnova.ventas.service;

import com.vetnova.ventas.event.EventoDominio;
import com.vetnova.ventas.exception.BusinessException;
import com.vetnova.ventas.exception.ResourceNotFoundException;
import com.vetnova.ventas.exception.ServiceUnavailableException;
import com.vetnova.ventas.model.Venta;
import com.vetnova.ventas.repository.VentaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
        venta = new Venta();
        venta.setId(1L);
        venta.setIdCliente(10L);
        venta.setIdProducto(100L);
        venta.setIdSucursal(1L);
        venta.setCantidad(2);
        venta.setMontoTotal(50000.0);
        venta.setEstado("PENDIENTE");
        venta.setFechaVenta(LocalDateTime.now());
    }

    @Test
    void testRegistrarVenta_Exito() {
        when(restTemplate.getForEntity(anyString(), eq(Object.class))).thenReturn(new org.springframework.http.ResponseEntity<>(HttpStatus.OK));
        when(ventaRepository.save(any(Venta.class))).thenReturn(venta);

        Venta resultado = ventaService.registrarVenta(venta);

        assertNotNull(resultado);
        assertEquals("PENDIENTE", resultado.getEstado());
        verify(ventaRepository, times(1)).save(venta);
    }

    @Test
    void testProcesarPago_ExitoYEventos() {
        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));
        when(ventaRepository.save(any(Venta.class))).thenReturn(venta);

        Venta resultado = ventaService.procesarPago(1L);

        assertNotNull(resultado);
        assertEquals("PAGADA", resultado.getEstado());
        // El mandato exige 2 eventos al pagar: PagoConfirmado y VentaConfirmada (para el inventario)
        verify(eventPublisher, times(2)).publishEvent(any(EventoDominio.class));
    }

    @Test
    void testRegistrarDevolucion_ExitoYEvento() {
        venta.setEstado("PAGADA");
        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));
        when(ventaRepository.save(any(Venta.class))).thenReturn(venta);

        Venta resultado = ventaService.registrarDevolucion(1L);

        assertNotNull(resultado);
        assertEquals("DEVUELTA", resultado.getEstado());
        // Verifica que se emitió el evento DevolucionRegistrada
        verify(eventPublisher, times(1)).publishEvent(any(EventoDominio.class));
    }

    @Test
    void procesarPago_siLaVentaYaEstaPagada_debeLanzarBusinessException() {
        venta.setEstado("PAGADA");
        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));

        BusinessException ex = assertThrows(BusinessException.class, () -> ventaService.procesarPago(1L));

        assertEquals("La venta ya se encuentra PAGADA.", ex.getMessage());
        verify(ventaRepository, never()).save(any(Venta.class));
    }

    @Test
    void registrarDevolucion_siLaVentaNoEstaPagada_debeLanzarBusinessException() {
        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));

        BusinessException ex = assertThrows(BusinessException.class, () -> ventaService.registrarDevolucion(1L));

        assertEquals("Solo se pueden devolver ventas en estado PAGADA.", ex.getMessage());
        verify(ventaRepository, never()).save(any(Venta.class));
    }

    @Test
    void registrarVenta_siElClienteNoExiste_debeLanzarResourceNotFoundException() {
        when(restTemplate.getForEntity(anyString(), eq(Object.class))).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> ventaService.registrarVenta(venta));

        assertTrue(ex.getMessage().contains("cliente"));
        verify(ventaRepository, never()).save(any(Venta.class));
    }

    @Test
    void registrarVenta_siElServicioClientesEstaCaido_debeLanzarServiceUnavailableException() {
        when(restTemplate.getForEntity(anyString(), eq(Object.class))).thenThrow(new ResourceAccessException("down"));

        ServiceUnavailableException ex = assertThrows(ServiceUnavailableException.class, () -> ventaService.registrarVenta(venta));

        assertTrue(ex.getMessage().contains("Clientes"));
        verify(ventaRepository, never()).save(any(Venta.class));
    }

    @Test
    void registrarVenta_siElProductoNoExiste_debeLanzarResourceNotFoundException() {
        when(restTemplate.getForEntity(anyString(), eq(Object.class)))
                .thenReturn(new org.springframework.http.ResponseEntity<>(HttpStatus.OK))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> ventaService.registrarVenta(venta));

        assertTrue(ex.getMessage().contains("producto"));
        verify(ventaRepository, never()).save(any(Venta.class));
    }

    @Test
    void registrarVenta_siLaSucursalNoExiste_debeLanzarResourceNotFoundException() {
        when(restTemplate.getForEntity(anyString(), eq(Object.class)))
                .thenReturn(new org.springframework.http.ResponseEntity<>(HttpStatus.OK))
                .thenReturn(new org.springframework.http.ResponseEntity<>(HttpStatus.OK))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> ventaService.registrarVenta(venta));

        assertTrue(ex.getMessage().contains("sucursal"));
        verify(ventaRepository, never()).save(any(Venta.class));
    }

    @Test
    void testEmitirBoleta_Exito() {
        // Para emitir boleta, la venta DEBE estar pagada
        venta.setEstado("PAGADA");
        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));

        Venta resultado = ventaService.emitirBoleta(1L);

        assertNotNull(resultado);
        assertEquals("PAGADA", resultado.getEstado());
    }

    @Test
    void testEmitirBoleta_FalloPorNoPagada() {
        // Si la venta está PENDIENTE, debe lanzar RuntimeException
        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));

        assertThrows(RuntimeException.class, () -> {
            ventaService.emitirBoleta(1L);
        });
    }
}