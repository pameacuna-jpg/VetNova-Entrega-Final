package com.vetnova.inventario.service;

import com.vetnova.inventario.dto.ProductoRequestDTO;
import com.vetnova.inventario.dto.ProductoResponseDTO;
import com.vetnova.inventario.model.Producto;
import com.vetnova.inventario.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    public List<ProductoResponseDTO> listarProductos() {
        return productoRepository.findByActivoTrue()
                .stream()
                .map(this::mapearAResponse)
                .toList();
    }

    public ProductoResponseDTO buscarPorId(Long id) {
        Producto producto = obtenerEntidadPorId(id);
        return mapearAResponse(producto);
    }

    public ProductoResponseDTO crearProducto(ProductoRequestDTO dto) {
        Producto producto = new Producto();

        producto.setNombre(dto.getNombre());
        producto.setCategoria(dto.getCategoria());
        producto.setPrecio(dto.getPrecio());
        producto.setStockActual(dto.getStockActual());
        producto.setStockMinimo(dto.getStockMinimo());
        producto.setActivo(true);

        return mapearAResponse(productoRepository.save(producto));
    }

    public ProductoResponseDTO actualizarProducto(Long id, ProductoRequestDTO dto) {
        Producto producto = obtenerEntidadPorId(id);

        producto.setNombre(dto.getNombre());
        producto.setCategoria(dto.getCategoria());
        producto.setPrecio(dto.getPrecio());
        producto.setStockActual(dto.getStockActual());
        producto.setStockMinimo(dto.getStockMinimo());

        return mapearAResponse(productoRepository.save(producto));
    }

    public void eliminarProducto(Long id) {
        Producto producto = obtenerEntidadPorId(id);
        producto.setActivo(false);
        productoRepository.save(producto);
    }

    public List<ProductoResponseDTO> buscarPorCategoria(String categoria) {
        return productoRepository.findByCategoriaIgnoreCase(categoria)
                .stream()
                .map(this::mapearAResponse)
                .toList();
    }

    public List<ProductoResponseDTO> listarProductosBajoStock() {
        return productoRepository.findAll()
                .stream()
                .filter(p -> Boolean.TRUE.equals(p.getActivo()))
                .filter(p -> p.getStockActual() <= p.getStockMinimo())
                .map(this::mapearAResponse)
                .toList();
    }

    public Producto obtenerEntidadPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
    }

    private ProductoResponseDTO mapearAResponse(Producto producto) {
        return new ProductoResponseDTO(
                producto.getIdProducto(),
                producto.getNombre(),
                producto.getCategoria(),
                producto.getPrecio(),
                producto.getStockActual(),
                producto.getStockMinimo(),
                producto.getActivo()
        );
    }
}