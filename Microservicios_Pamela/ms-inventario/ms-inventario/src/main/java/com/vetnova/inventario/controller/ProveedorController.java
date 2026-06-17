package com.vetnova.inventario.controller;

import com.vetnova.inventario.model.Proveedor;
import com.vetnova.inventario.service.ProveedorService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.List;

@RestController
@RequestMapping("/api/v1/proveedores")
public class ProveedorController {

    private final ProveedorService proveedorService;

    public ProveedorController(ProveedorService proveedorService) {
        this.proveedorService = proveedorService;
    }

    @GetMapping
    public List<Proveedor> listarProveedores() {
        return proveedorService.listarProveedores();
    }

    @GetMapping("/{id}")
    public Proveedor buscarPorId(@PathVariable Long id) {
        return proveedorService.buscarPorId(id);
    }
    @PostMapping
    public ResponseEntity<Proveedor> crearProveedor(@Valid @RequestBody Proveedor proveedor) {
        return ResponseEntity.ok(proveedorService.crearProveedor(proveedor));
    }


    @PutMapping("/{id}")
    public Proveedor actualizarProveedor(@PathVariable Long id, @Valid @RequestBody Proveedor proveedor) {
        return proveedorService.actualizarProveedor(id, proveedor);
    }

    @DeleteMapping("/{id}")
    public String eliminarProveedor(@PathVariable Long id) {
        proveedorService.eliminarProveedor(id);
        return "Proveedor eliminado correctamente";
    }

    @GetMapping("/buscar/{nombre}")
    public List<Proveedor> buscarPorNombre(@PathVariable String nombre) {
        return proveedorService.buscarPorNombre(nombre);
    }
}