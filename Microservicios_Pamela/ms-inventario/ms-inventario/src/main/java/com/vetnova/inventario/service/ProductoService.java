package com.vetnova.inventario.service;

import com.vetnova.inventario.model.Producto;
import com.vetnova.inventario.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    public List<Producto> listarProductos() {
        return productoRepository.findByActivoTrue();
    }

    public Producto buscarPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
    }

    public Producto crearProducto(Producto producto) {
        producto.setActivo(true);
        return productoRepository.save(producto);
    }

    public Producto actualizarProducto(Long id, Producto datos) {
        Producto producto = buscarPorId(id);

        producto.setNombre(datos.getNombre());
        producto.setCategoria(datos.getCategoria());
        producto.setPrecio(datos.getPrecio());
        producto.setStockActual(datos.getStockActual());
        producto.setStockMinimo(datos.getStockMinimo());

        return productoRepository.save(producto);
    }

    public void eliminarProducto(Long id) {
        Producto producto = buscarPorId(id);
        producto.setActivo(false);
        productoRepository.save(producto);
    }

    public List<Producto> buscarPorCategoria(String categoria) {
        return productoRepository.findByCategoriaIgnoreCase(categoria);
    }

    public List<Producto> listarProductosBajoStock() {
        return productoRepository.findAll()
                .stream()
                .filter(p -> p.getActivo() && p.getStockActual() <= p.getStockMinimo())
                .toList();
    }
}
