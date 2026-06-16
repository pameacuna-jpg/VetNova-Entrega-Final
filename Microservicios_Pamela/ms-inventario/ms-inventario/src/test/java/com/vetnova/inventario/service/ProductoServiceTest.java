package com.vetnova.inventario.service;

import com.vetnova.inventario.dto.ProductoRequestDTO;
import com.vetnova.inventario.dto.ProductoResponseDTO;
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
    private ProductoRequestDTO productoRequestDTO;

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

        productoRequestDTO = new ProductoRequestDTO();
        productoRequestDTO.setNombre("Vacuna Rabia");
        productoRequestDTO.setCategoria("Vacunas");
        productoRequestDTO.setPrecio(15000);
        productoRequestDTO.setStockActual(10);
        productoRequestDTO.setStockMinimo(5);
    }

    @Test
    void crearProducto_deberiaGuardarProducto() {
        when(productoRepository.save(any(Producto.class)))
                .thenReturn(producto);

        ProductoResponseDTO resultado =
                productoService.crearProducto(productoRequestDTO);

        assertNotNull(resultado);
        assertEquals("Vacuna Rabia", resultado.getNombre());
        assertEquals("Vacunas", resultado.getCategoria());
        assertTrue(resultado.getActivo());

        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    void listarProductos_deberiaRetornarLista() {
        when(productoRepository.findByActivoTrue())
                .thenReturn(Arrays.asList(producto));

        List<ProductoResponseDTO> resultado =
                productoService.listarProductos();

        assertEquals(1, resultado.size());
        assertEquals("Vacuna Rabia", resultado.get(0).getNombre());

        verify(productoRepository).findByActivoTrue();
    }

    @Test
    void buscarPorId_deberiaRetornarProducto() {
        when(productoRepository.findById(1L))
                .thenReturn(Optional.of(producto));

        ProductoResponseDTO resultado =
                productoService.buscarPorId(1L);

        assertEquals("Vacuna Rabia", resultado.getNombre());
        assertEquals(1L, resultado.getIdProducto());

        verify(productoRepository).findById(1L);
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

    @Test
    void actualizarProducto_deberiaModificarProducto() {
        ProductoRequestDTO actualizado = new ProductoRequestDTO();
        actualizado.setNombre("Vacuna Triple");
        actualizado.setCategoria("Vacunas");
        actualizado.setPrecio(18000);
        actualizado.setStockActual(20);
        actualizado.setStockMinimo(10);

        when(productoRepository.findById(1L))
                .thenReturn(Optional.of(producto));

        when(productoRepository.save(any(Producto.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProductoResponseDTO resultado =
                productoService.actualizarProducto(1L, actualizado);

        assertEquals("Vacuna Triple", resultado.getNombre());
        assertEquals(18000, resultado.getPrecio());
        assertEquals(20, resultado.getStockActual());
        assertEquals(10, resultado.getStockMinimo());

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

        List<ProductoResponseDTO> resultado =
                productoService.buscarPorCategoria("Vacunas");

        assertEquals(1, resultado.size());
        assertEquals("Vacunas", resultado.get(0).getCategoria());

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

        List<ProductoResponseDTO> resultado =
                productoService.listarProductosBajoStock();

        assertEquals(1, resultado.size());
        assertEquals("Antibiótico", resultado.get(0).getNombre());
    }
}