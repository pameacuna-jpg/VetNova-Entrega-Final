package com.vetnova.atencionclinica.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vetnova.atencionclinica.dto.CertificadoRequestDTO;
import com.vetnova.atencionclinica.dto.DiagnosticoRequestDTO;
import com.vetnova.atencionclinica.dto.DiagnosticoResponseDTO;
import com.vetnova.atencionclinica.dto.RecetaRequestDTO;
import com.vetnova.atencionclinica.dto.TratamientoRequestDTO;
import com.vetnova.atencionclinica.service.DiagnosticoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/atenciones")
@RequiredArgsConstructor
public class AtencionController {

    private final DiagnosticoService service;

    @GetMapping("/id/{id}")
    public ResponseEntity<DiagnosticoResponseDTO> buscarAtencionPorId(@PathVariable Long id) {
        DiagnosticoResponseDTO response = service.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<DiagnosticoResponseDTO> registrarDiagnostico(
            @Valid @RequestBody DiagnosticoRequestDTO request) {

        DiagnosticoResponseDTO response = service.registrarDiagnostico(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}/tratamiento")
    public ResponseEntity<DiagnosticoResponseDTO> registrarTratamiento(
            @PathVariable Long id,
            @Valid @RequestBody TratamientoRequestDTO request) {

        DiagnosticoResponseDTO response = service.registrarTratamiento(id, request.getTratamiento());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/receta")
    public ResponseEntity<DiagnosticoResponseDTO> emitirReceta(
            @PathVariable Long id,
            @Valid @RequestBody RecetaRequestDTO request) {

        DiagnosticoResponseDTO response = service.emitirReceta(id, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/certificado")
    public ResponseEntity<DiagnosticoResponseDTO> emitirCertificado(
            @PathVariable Long id,
            @Valid @RequestBody CertificadoRequestDTO request) {

        DiagnosticoResponseDTO response = service.emitirCertificado(id, request.getDetalleCertificado());
        return ResponseEntity.ok(response);
    }
}