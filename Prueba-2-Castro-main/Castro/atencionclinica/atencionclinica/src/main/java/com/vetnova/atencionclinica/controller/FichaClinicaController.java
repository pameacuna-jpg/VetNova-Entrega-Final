package com.vetnova.atencionclinica.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vetnova.atencionclinica.dto.FichaClinicaRequestDTO;
import com.vetnova.atencionclinica.dto.FichaClinicaResponseDTO;
import com.vetnova.atencionclinica.service.FichaClinicaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/fichas")
@RequiredArgsConstructor
public class FichaClinicaController {

    private final FichaClinicaService service;

    @PostMapping
    public ResponseEntity<FichaClinicaResponseDTO> crearFicha(@Valid @RequestBody FichaClinicaRequestDTO request) {
        FichaClinicaResponseDTO response = service.crearFicha(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}