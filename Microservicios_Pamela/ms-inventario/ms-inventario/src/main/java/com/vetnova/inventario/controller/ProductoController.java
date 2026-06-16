package com.vetnova.inventario.controller;

import com.vetnova.inventario.dto.ProductoRequestDTO;
import com.vetnova.inventario.dto.ProductoResponseDTO;
import com.vetnova.inventario.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @GetMapping
    public ResponseEntity<List<ProductoResponseDTO>> listarProductos() {
        return ResponseEntity.ok(productoService.listarProductos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<ProductoResponseDTO> crearProducto(
            @Valid @RequestBody ProductoRequestDTO productoRequestDTO) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(productoService.crearProducto(productoRequestDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> actualizarProducto(
            @PathVariable Long id,
            @Valid @RequestBody ProductoRequestDTO productoRequestDTO) {

        return ResponseEntity.ok(
                productoService.actualizarProducto(id, productoRequestDTO)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        productoService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<ProductoResponseDTO>> buscarPorCategoria(
            @PathVariable String categoria) {

        return ResponseEntity.ok(productoService.buscarPorCategoria(categoria));
    }

    @GetMapping("/bajo-stock")
    public ResponseEntity<List<ProductoResponseDTO>> listarProductosBajoStock() {
        return ResponseEntity.ok(productoService.listarProductosBajoStock());
    }
}