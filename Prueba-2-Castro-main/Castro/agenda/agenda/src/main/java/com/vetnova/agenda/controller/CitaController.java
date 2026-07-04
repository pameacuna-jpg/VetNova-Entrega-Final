package com.vetnova.agenda.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vetnova.agenda.dto.CitaRequestDTO;
import com.vetnova.agenda.dto.CitaResponseDTO;
import com.vetnova.agenda.service.CitaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/citas")
@RequiredArgsConstructor
@Tag(name = "Citas Médicas", description = "Operaciones para agendar, reprogramar, cancelar y confirmar citas")
public class CitaController {

    private final CitaService citaService;

    @PostMapping("/agendar")
    @Operation(summary = "Agendar hora")
    public ResponseEntity<CitaResponseDTO> agendarHora(@Valid @RequestBody CitaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(citaService.agendarHora(dto));
    }

    @PutMapping("/{id}/reprogramar")
    @Operation(summary = "Reprogramar cita")
    public ResponseEntity<CitaResponseDTO> reprogramarHora(@PathVariable Long id,
                                                           @RequestBody Map<String, String> request) {
        LocalDateTime nuevaFecha = LocalDateTime.parse(request.get("nuevaFechaHora"));
        return ResponseEntity.ok(citaService.reprogramarHora(id, nuevaFecha));
    }

    @PutMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar cita")
    public ResponseEntity<CitaResponseDTO> cancelarHora(@PathVariable Long id) {
        return ResponseEntity.ok(citaService.cancelarHora(id));
    }

    @PutMapping("/{id}/confirmar")
    @Operation(summary = "Confirmar asistencia")
    public ResponseEntity<CitaResponseDTO> confirmarAsistencia(@PathVariable Long id) {
        return ResponseEntity.ok(citaService.confirmarAsistencia(id));
    }

    @GetMapping
    @Operation(summary = "Listar agenda")
    public ResponseEntity<List<CitaResponseDTO>> listarCitas() {
        return ResponseEntity.ok(citaService.obtenerTodasLasCitas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar cita")
    public ResponseEntity<CitaResponseDTO> obtenerCita(@PathVariable Long id) {
        return ResponseEntity.ok(citaService.obtenerCitaPorId(id));
    }

    @GetMapping("/manana")
    @Operation(summary = "Obtener citas próximas 24 horas")
    public ResponseEntity<List<CitaResponseDTO>> obtenerCitasDeManana() {
        return ResponseEntity.ok(citaService.obtenerCitasProximas24h());
    }
}