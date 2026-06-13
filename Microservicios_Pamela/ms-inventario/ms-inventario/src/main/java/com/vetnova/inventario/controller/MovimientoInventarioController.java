package com.vetnova.inventario.controller;

import com.vetnova.inventario.model.MovimientoInventario;
import com.vetnova.inventario.service.MovimientoInventarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.List;

@RestController
@RequestMapping("/api/v1/movimientos")
public class MovimientoInventarioController {

    @Autowired
    private MovimientoInventarioService movimientoService;

    @GetMapping
    public List<MovimientoInventario> listarMovimientos() {

        return movimientoService.listarMovimientos();
    }

    @GetMapping("/{id}")
    public MovimientoInventario buscarPorId(@PathVariable Long id) {

        return movimientoService.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<MovimientoInventario> registrarMovimiento(    
        @Valid @RequestBody MovimientoInventario movimiento) {

    return ResponseEntity.ok(movimientoService.registrarMovimiento(movimiento));
    }


    @GetMapping("/producto/{idProducto}")
    public List<MovimientoInventario> buscarPorProducto(
            @PathVariable Long idProducto) {

        return movimientoService.buscarPorProducto(idProducto);
    }

    @GetMapping("/sucursal/{idSucursal}")
    public List<MovimientoInventario> buscarPorSucursal(
            @PathVariable Long idSucursal) {

        return movimientoService.buscarPorSucursal(idSucursal);
    }

    @GetMapping("/tipo/{tipo}")
    public List<MovimientoInventario> buscarPorTipo(
            @PathVariable String tipo) {

        return movimientoService.buscarPorTipo(tipo);
    }
}