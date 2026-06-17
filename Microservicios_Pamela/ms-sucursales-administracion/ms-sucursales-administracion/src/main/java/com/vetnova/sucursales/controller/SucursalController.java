package com.vetnova.sucursales.controller;

import com.vetnova.sucursales.model.Sucursal;
import com.vetnova.sucursales.service.SucursalService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.List;

@RestController
@RequestMapping("/api/v1/sucursales")
public class SucursalController {


    private final SucursalService sucursalService;

    public SucursalController(SucursalService sucursalService) {
        this.sucursalService = sucursalService;
    }

    @GetMapping
    public List<Sucursal> listarSucursales() {
        return sucursalService.listarSucursales();
    }

    @GetMapping("/activas")
    public List<Sucursal> listarSucursalesActivas() {
        return sucursalService.listarSucursalesActivas();
    }

    @GetMapping("/{id}")
    public Sucursal buscarPorId(@PathVariable Long id) {
        return sucursalService.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<Sucursal> crearSucursal(@Valid @RequestBody Sucursal sucursal) {
    return ResponseEntity.ok(sucursalService.crearSucursal(sucursal));
    }
    
    @PutMapping("/{id}")
    public Sucursal actualizarSucursal(@PathVariable Long id, @Valid @RequestBody Sucursal sucursal) {
        return sucursalService.actualizarSucursal(id, sucursal);
    }

    @DeleteMapping("/{id}")
    public String desactivarSucursal(@PathVariable Long id) {
        sucursalService.desactivarSucursal(id);
        return "Sucursal desactivada correctamente";
    }

    @GetMapping("/ciudad/{ciudad}")
    public List<Sucursal> buscarPorCiudad(@PathVariable String ciudad) {
        return sucursalService.buscarPorCiudad(ciudad);
    }
}