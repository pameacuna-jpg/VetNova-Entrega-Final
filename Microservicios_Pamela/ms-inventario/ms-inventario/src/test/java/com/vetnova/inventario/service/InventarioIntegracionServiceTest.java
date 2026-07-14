package com.vetnova.inventario.service;

import com.vetnova.inventario.client.SucursalClient;
import com.vetnova.inventario.dto.ConsumoInventarioRequestDTO;
import com.vetnova.inventario.dto.ConsumoInventarioResponseDTO;
import com.vetnova.inventario.dto.DisponibilidadProductoResponseDTO;
import com.vetnova.inventario.enums.OrigenConsumo;
import com.vetnova.inventario.model.MovimientoInventario;
import com.vetnova.inventario.model.Producto;
import com.vetnova.inventario.repository.MovimientoInventarioRepository;
import com.vetnova.inventario.repository.ProductoRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventarioIntegracionServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private MovimientoInventarioRepository movimientoRepository;

    @Mock
    private SucursalClient sucursalClient;

    @Mock
    private MovimientoInventarioService movimientoInventarioService;

    @InjectMocks
    private InventarioIntegracionService service;

    private Producto producto;

    @BeforeEach
    void setUp() {
        producto = new Producto(1L, "Vacuna Rabia", "Vacunas", 15000, 10, 5, true);
    }

    // ---- consultarDisponibilidad ----

    @Test
    void consultarDisponibilidad_conCantidadNula_debeLanzarExcepcion() {
        assertThrows(RuntimeException.class,
                () -> service.consultarDisponibilidad(1L, null));
    }

    @Test
    void consultarDisponibilidad_conCantidadCeroONegativa_debeLanzarExcepcion() {
        assertThrows(RuntimeException.class,
                () -> service.consultarDisponibilidad(1L, 0));
    }

    @Test
    void consultarDisponibilidad_conProductoInexistente_debeLanzarExcepcion() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> service.consultarDisponibilidad(99L, 1));
    }

    @Test
    void consultarDisponibilidad_conProductoInactivo_debeRetornarNoDisponible() {
        producto.setActivo(false);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        DisponibilidadProductoResponseDTO resultado =
                service.consultarDisponibilidad(1L, 1);

        assertFalse(resultado.isDisponible());
        assertFalse(resultado.isActivo());
        assertEquals("El producto se encuentra inactivo", resultado.getMensaje());
    }

    @Test
    void consultarDisponibilidad_conStockInsuficiente_debeRetornarNoDisponible() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        DisponibilidadProductoResponseDTO resultado =
                service.consultarDisponibilidad(1L, 999);

        assertFalse(resultado.isDisponible());
        assertTrue(resultado.isActivo());
        assertEquals("Stock insuficiente", resultado.getMensaje());
    }

    @Test
    void consultarDisponibilidad_conStockSuficiente_debeRetornarDisponible() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        DisponibilidadProductoResponseDTO resultado =
                service.consultarDisponibilidad(1L, 5);

        assertTrue(resultado.isDisponible());
        assertTrue(resultado.isActivo());
        assertEquals("Producto disponible", resultado.getMensaje());
        assertEquals(1L, resultado.getIdProducto());
        assertEquals("Vacuna Rabia", resultado.getNombreProducto());
    }

    // ---- registrarConsumo ----

    @Test
    void registrarConsumo_conProductoInexistente_debeLanzarExcepcion() {
        ConsumoInventarioRequestDTO request = new ConsumoInventarioRequestDTO(
                99L, 1L, 2, OrigenConsumo.VENTA, 10L, "obs"
        );
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.registrarConsumo(request));
    }

    @Test
    void registrarConsumo_conProductoInactivo_debeLanzarExcepcion() {
        producto.setActivo(false);
        ConsumoInventarioRequestDTO request = new ConsumoInventarioRequestDTO(
                1L, 1L, 2, OrigenConsumo.VENTA, 10L, "obs"
        );
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        assertThrows(RuntimeException.class, () -> service.registrarConsumo(request));
    }

    @Test
    void registrarConsumo_conStockInsuficiente_debeLanzarExcepcion() {
        ConsumoInventarioRequestDTO request = new ConsumoInventarioRequestDTO(
                1L, 1L, 999, OrigenConsumo.VENTA, 10L, "obs"
        );
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        assertThrows(RuntimeException.class, () -> service.registrarConsumo(request));
    }

    @Test
    void registrarConsumo_conDatosValidos_debeRegistrarYRetornarResponse() {
        ConsumoInventarioRequestDTO request = new ConsumoInventarioRequestDTO(
                1L, 1L, 2, OrigenConsumo.VENTA, 10L, "obs"
        );

        Producto productoActualizado =
                new Producto(1L, "Vacuna Rabia", "Vacunas", 15000, 8, 5, true);

        MovimientoInventario movimientoGuardado = new MovimientoInventario();
        movimientoGuardado.setIdMovimiento(50L);

        when(productoRepository.findById(1L))
                .thenReturn(Optional.of(producto))
                .thenReturn(Optional.of(productoActualizado));
        when(movimientoInventarioService.registrarMovimiento(any(MovimientoInventario.class)))
                .thenReturn(movimientoGuardado);

        ConsumoInventarioResponseDTO resultado = service.registrarConsumo(request);

        assertNotNull(resultado);
        assertEquals(50L, resultado.getIdMovimiento());
        assertEquals(1L, resultado.getIdProducto());
        assertEquals(1L, resultado.getIdSucursal());
        assertEquals(2, resultado.getCantidadConsumida());
        assertEquals(10, resultado.getStockAnterior());
        assertEquals(8, resultado.getStockActual());
        assertEquals(OrigenConsumo.VENTA, resultado.getOrigen());
        assertEquals(10L, resultado.getIdReferencia());

        verify(sucursalClient).validarSucursal(1L);
        verify(movimientoInventarioService).registrarMovimiento(any(MovimientoInventario.class));
    }

    @Test
    void registrarConsumo_conProductoNoEncontradoDespuesDeActualizar_debeLanzarExcepcion() {
        ConsumoInventarioRequestDTO request = new ConsumoInventarioRequestDTO(
                1L, 1L, 2, OrigenConsumo.VENTA, 10L, "obs"
        );

        MovimientoInventario movimientoGuardado = new MovimientoInventario();
        movimientoGuardado.setIdMovimiento(50L);

        when(productoRepository.findById(1L))
                .thenReturn(Optional.of(producto))
                .thenReturn(Optional.empty());
        when(movimientoInventarioService.registrarMovimiento(any(MovimientoInventario.class)))
                .thenReturn(movimientoGuardado);

        assertThrows(RuntimeException.class, () -> service.registrarConsumo(request));
    }
}
