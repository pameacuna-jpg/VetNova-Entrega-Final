package com.vetnova.inventario.service;

import com.vetnova.inventario.client.SucursalClient;
import com.vetnova.inventario.dto.StockBajoEvent;
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
import org.springframework.context.ApplicationEventPublisher;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MovimientoInventarioServiceTest {

    @Mock
    private MovimientoInventarioRepository movimientoRepository;

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private SucursalClient sucursalClient;

    @InjectMocks
    private MovimientoInventarioService movimientoService;

    private Producto producto;
    private MovimientoInventario movimiento;

    @BeforeEach
    void setUp() {
        producto = new Producto(
                1L,
                "Vacuna Rabia",
                "Vacunas",
                15000,
                10,
                5,
                true
        );

        movimiento = new MovimientoInventario();
        movimiento.setIdMovimiento(1L);
        movimiento.setIdProducto(1L);
        movimiento.setIdSucursal(1L);
        movimiento.setTipoMovimiento("ENTRADA");
        movimiento.setCantidad(5);
    }

    @Test
    void listarMovimientos_deberiaRetornarLista() {
        when(movimientoRepository.findAll()).thenReturn(Arrays.asList(movimiento));

        List<MovimientoInventario> resultado = movimientoService.listarMovimientos();

        assertEquals(1, resultado.size());
        verify(movimientoRepository).findAll();
    }

    @Test
    void buscarPorId_cuandoExiste_deberiaRetornarMovimiento() {
        when(movimientoRepository.findById(1L)).thenReturn(Optional.of(movimiento));

        MovimientoInventario resultado = movimientoService.buscarPorId(1L);

        assertEquals(1L, resultado.getIdMovimiento());
        verify(movimientoRepository).findById(1L);
    }

    @Test
    void buscarPorId_cuandoNoExiste_deberiaLanzarExcepcion() {
        when(movimientoRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> movimientoService.buscarPorId(99L)
        );

        assertEquals("Movimiento no encontrado con ID: 99", exception.getMessage());
        verify(movimientoRepository).findById(99L);
    }

    @Test
    void registrarMovimiento_entrada_deberiaAumentarStock() {
        movimiento.setTipoMovimiento("ENTRADA");
        movimiento.setCantidad(5);

        doNothing().when(sucursalClient).validarSucursal(1L);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);
        when(movimientoRepository.save(any(MovimientoInventario.class))).thenReturn(movimiento);

        MovimientoInventario resultado = movimientoService.registrarMovimiento(movimiento);

        assertNotNull(resultado);
        assertEquals(15, producto.getStockActual());

        verify(sucursalClient).validarSucursal(1L);
        verify(productoRepository).save(producto);
        verify(movimientoRepository).save(movimiento);
        verify(eventPublisher, never()).publishEvent(any(StockBajoEvent.class));
    }

    @Test
    void registrarMovimiento_salida_deberiaDisminuirStock() {
        movimiento.setTipoMovimiento("SALIDA");
        movimiento.setCantidad(3);

        doNothing().when(sucursalClient).validarSucursal(1L);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);
        when(movimientoRepository.save(any(MovimientoInventario.class))).thenReturn(movimiento);

        MovimientoInventario resultado = movimientoService.registrarMovimiento(movimiento);

        assertNotNull(resultado);
        assertEquals(7, producto.getStockActual());

        verify(sucursalClient).validarSucursal(1L);
        verify(productoRepository).save(producto);
        verify(movimientoRepository).save(movimiento);
        verify(eventPublisher, never()).publishEvent(any(StockBajoEvent.class));
    }

    @Test
    void registrarMovimiento_salidaConStockInsuficiente_deberiaLanzarExcepcion() {
        movimiento.setTipoMovimiento("SALIDA");
        movimiento.setCantidad(20);

        doNothing().when(sucursalClient).validarSucursal(1L);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> movimientoService.registrarMovimiento(movimiento)
        );

        assertEquals("Stock insuficiente para realizar la salida", exception.getMessage());

        verify(sucursalClient).validarSucursal(1L);
        verify(productoRepository, never()).save(any(Producto.class));
        verify(movimientoRepository, never()).save(any(MovimientoInventario.class));
        verify(eventPublisher, never()).publishEvent(any(StockBajoEvent.class));
    }

    @Test
    void registrarMovimiento_ajuste_deberiaActualizarStockAlValorIndicado() {
        movimiento.setTipoMovimiento("AJUSTE");
        movimiento.setCantidad(25);

        doNothing().when(sucursalClient).validarSucursal(1L);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);
        when(movimientoRepository.save(any(MovimientoInventario.class))).thenReturn(movimiento);

        MovimientoInventario resultado = movimientoService.registrarMovimiento(movimiento);

        assertNotNull(resultado);
        assertEquals(25, producto.getStockActual());

        verify(sucursalClient).validarSucursal(1L);
        verify(productoRepository).save(producto);
        verify(movimientoRepository).save(movimiento);
        verify(eventPublisher, never()).publishEvent(any(StockBajoEvent.class));
    }

    @Test
    void registrarMovimiento_tipoInvalido_deberiaLanzarExcepcion() {
        movimiento.setTipoMovimiento("TRASLADO");

        doNothing().when(sucursalClient).validarSucursal(1L);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> movimientoService.registrarMovimiento(movimiento)
        );

        assertEquals("Tipo de movimiento inválido. Use ENTRADA, SALIDA o AJUSTE", exception.getMessage());

        verify(sucursalClient).validarSucursal(1L);
        verify(productoRepository, never()).save(any(Producto.class));
        verify(movimientoRepository, never()).save(any(MovimientoInventario.class));
        verify(eventPublisher, never()).publishEvent(any(StockBajoEvent.class));
    }

    @Test
    void registrarMovimiento_productoNoExiste_deberiaLanzarExcepcion() {
        doNothing().when(sucursalClient).validarSucursal(1L);
        when(productoRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> movimientoService.registrarMovimiento(movimiento)
        );

        assertEquals("Producto no encontrado con ID: 1", exception.getMessage());

        verify(sucursalClient).validarSucursal(1L);
        verify(productoRepository, never()).save(any(Producto.class));
        verify(movimientoRepository, never()).save(any(MovimientoInventario.class));
        verify(eventPublisher, never()).publishEvent(any(StockBajoEvent.class));
    }

    @Test
    void registrarMovimiento_cuandoStockQuedaBajo_deberiaPublicarEventoStockBajo() {
        movimiento.setTipoMovimiento("SALIDA");
        movimiento.setCantidad(6);

        doNothing().when(sucursalClient).validarSucursal(1L);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);
        when(movimientoRepository.save(any(MovimientoInventario.class))).thenReturn(movimiento);

        movimientoService.registrarMovimiento(movimiento);

        assertEquals(4, producto.getStockActual());

        verify(sucursalClient).validarSucursal(1L);
        verify(eventPublisher).publishEvent(any(StockBajoEvent.class));
    }

    @Test
    void buscarPorProducto_deberiaRetornarMovimientos() {
        when(movimientoRepository.findByIdProducto(1L)).thenReturn(Arrays.asList(movimiento));

        List<MovimientoInventario> resultado = movimientoService.buscarPorProducto(1L);

        assertEquals(1, resultado.size());
        verify(movimientoRepository).findByIdProducto(1L);
    }

    @Test
    void buscarPorSucursal_deberiaRetornarMovimientos() {
        when(movimientoRepository.findByIdSucursal(1L)).thenReturn(Arrays.asList(movimiento));

        List<MovimientoInventario> resultado = movimientoService.buscarPorSucursal(1L);

        assertEquals(1, resultado.size());
        verify(movimientoRepository).findByIdSucursal(1L);
    }

    @Test
    void buscarPorTipo_deberiaRetornarMovimientos() {
        when(movimientoRepository.findByTipoMovimientoIgnoreCase("ENTRADA"))
                .thenReturn(Arrays.asList(movimiento));

        List<MovimientoInventario> resultado = movimientoService.buscarPorTipo("ENTRADA");

        assertEquals(1, resultado.size());
        verify(movimientoRepository).findByTipoMovimientoIgnoreCase("ENTRADA");
    }
}