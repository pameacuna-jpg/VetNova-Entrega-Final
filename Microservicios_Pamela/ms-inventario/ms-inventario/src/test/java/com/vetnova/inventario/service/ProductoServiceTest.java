package com.vetnova.inventario.service;

import com.vetnova.inventario.model.Producto;
import com.vetnova.inventario.repository.ProductoRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoService productoService;

    private Producto producto;

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
    }

    @Test
    void crearProducto_deberiaGuardarProducto() {

        when(productoRepository.save(any(Producto.class)))
                .thenReturn(producto);

        Producto resultado = productoService.crearProducto(producto);

        assertNotNull(resultado);
        assertEquals("Vacuna Rabia", resultado.getNombre());

        verify(productoRepository, times(1)).save(producto);
    }

    @Test
    void listarProductos_deberiaRetornarLista() {

        when(productoRepository.findByActivoTrue())
                .thenReturn(Arrays.asList(producto));

        List<Producto> resultado = productoService.listarProductos();

        assertEquals(1, resultado.size());

        verify(productoRepository).findByActivoTrue();
    }

    @Test
    void buscarPorId_deberiaRetornarProducto() {

        when(productoRepository.findById(1L))
                .thenReturn(Optional.of(producto));

        Producto resultado = productoService.buscarPorId(1L);

        assertEquals("Vacuna Rabia", resultado.getNombre());

        verify(productoRepository).findById(1L);
    }

    @Test
    void actualizarProducto_deberiaModificarProducto() {

        Producto actualizado = new Producto(
                null,
                "Vacuna Triple",
                "Vacunas",
                18000,
                20,
                10,
                true
        );

        when(productoRepository.findById(1L))
                .thenReturn(Optional.of(producto));

        when(productoRepository.save(any(Producto.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Producto resultado =
                productoService.actualizarProducto(1L, actualizado);

        assertEquals("Vacuna Triple", resultado.getNombre());
        assertEquals(18000, resultado.getPrecio());

        verify(productoRepository).save(producto);
    }

    @Test
    void eliminarProducto_deberiaDesactivarProducto() {

        when(productoRepository.findById(1L))
                .thenReturn(Optional.of(producto));

        when(productoRepository.save(any(Producto.class)))
                .thenReturn(producto);

        productoService.eliminarProducto(1L);

        assertFalse(producto.getActivo());

        verify(productoRepository).save(producto);
    }

    @Test
    void buscarPorCategoria_deberiaRetornarProductos() {

        when(productoRepository.findByCategoriaIgnoreCase("Vacunas"))
                .thenReturn(Arrays.asList(producto));

        List<Producto> resultado =
                productoService.buscarPorCategoria("Vacunas");

        assertEquals(1, resultado.size());

        verify(productoRepository)
                .findByCategoriaIgnoreCase("Vacunas");
    }

    @Test
    void listarProductosBajoStock_deberiaRetornarProductosConStockBajo() {

        Producto bajoStock = new Producto(
                2L,
                "Antibiótico",
                "Medicamentos",
                5000,
                2,
                5,
                true
        );

        when(productoRepository.findAll())
                .thenReturn(Arrays.asList(producto, bajoStock));

        List<Producto> resultado =
                productoService.listarProductosBajoStock();

        assertEquals(1, resultado.size());
        assertEquals("Antibiótico", resultado.get(0).getNombre());
    }
    @Test
void buscarPorId_cuandoNoExiste_deberiaLanzarExcepcion() {

    when(productoRepository.findById(99L))
            .thenReturn(Optional.empty());

    RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> productoService.buscarPorId(99L)
    );

    assertEquals("Producto no encontrado con ID: 99", exception.getMessage());

    verify(productoRepository).findById(99L);
}
}