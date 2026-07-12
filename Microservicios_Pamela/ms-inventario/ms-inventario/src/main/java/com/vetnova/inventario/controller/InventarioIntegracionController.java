package com.vetnova.inventario.controller;

import com.vetnova.inventario.dto.ConsumoInventarioRequestDTO;
import com.vetnova.inventario.dto.ConsumoInventarioResponseDTO;
import com.vetnova.inventario.dto.DisponibilidadProductoResponseDTO;
import com.vetnova.inventario.service.InventarioIntegracionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/inventario")
@Validated
public class InventarioIntegracionController {

    private final InventarioIntegracionService inventarioIntegracionService;

    public InventarioIntegracionController(
            InventarioIntegracionService inventarioIntegracionService
    ) {
        this.inventarioIntegracionService = inventarioIntegracionService;
    }

    @GetMapping("/disponibilidad/{idProducto}")
    public ResponseEntity<DisponibilidadProductoResponseDTO> consultarDisponibilidad(
            @PathVariable Long idProducto,
            @RequestParam
            @Positive(message = "La cantidad debe ser mayor a cero")
            Integer cantidad
    ) {
        return ResponseEntity.ok(
                inventarioIntegracionService.consultarDisponibilidad(
                        idProducto,
                        cantidad
                )
        );
    }

    @PostMapping("/consumos")
    public ResponseEntity<ConsumoInventarioResponseDTO> registrarConsumo(
            @Valid @RequestBody ConsumoInventarioRequestDTO request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        inventarioIntegracionService.registrarConsumo(request)
                );
    }
}