package com.vetnova.agenda.service;

import java.time.LocalDateTime;
import java.util.List;

import com.vetnova.agenda.dto.CitaRequestDTO;
import com.vetnova.agenda.dto.CitaResponseDTO;

public interface CitaService {

    CitaResponseDTO agendarHora(CitaRequestDTO requestDTO);

    CitaResponseDTO reprogramarHora(Long id, LocalDateTime nuevaFecha);

    CitaResponseDTO cancelarHora(Long id);

    CitaResponseDTO confirmarAsistencia(Long id);

    List<CitaResponseDTO> obtenerTodasLasCitas();

    CitaResponseDTO obtenerCitaPorId(Long id);

    List<CitaResponseDTO> obtenerCitasProximas24h();
}