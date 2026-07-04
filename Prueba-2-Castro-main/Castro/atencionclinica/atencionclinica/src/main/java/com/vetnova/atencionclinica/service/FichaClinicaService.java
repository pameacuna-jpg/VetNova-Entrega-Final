package com.vetnova.atencionclinica.service;

import com.vetnova.atencionclinica.dto.FichaClinicaRequestDTO;
import com.vetnova.atencionclinica.dto.FichaClinicaResponseDTO;
import com.vetnova.atencionclinica.event.AtencionRegistradaEvent;
import com.vetnova.atencionclinica.exception.ResourceNotFoundException;
import com.vetnova.atencionclinica.model.FichaClinica;
import com.vetnova.atencionclinica.repository.FichaClinicaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FichaClinicaService {

    private final FichaClinicaRepository repository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public FichaClinicaResponseDTO crearFicha(FichaClinicaRequestDTO request) {
        FichaClinica ficha = mapToEntity(request);
        log.info("Creando ficha clínica para Mascota ID: {}", ficha.getIdMascota());
        ficha.setFechaCreacion(LocalDateTime.now());
        FichaClinica nuevaFicha = repository.save(ficha);

        AtencionRegistradaEvent evento = new AtencionRegistradaEvent(
                nuevaFicha.getIdFicha(),
                nuevaFicha.getIdMascota(),
                "REGISTRADA"
        );
        eventPublisher.publishEvent(evento);
        log.info("Evento [AtencionRegistrada] emitido exitosamente.");

        return mapToResponse(nuevaFicha);
    }

    public List<FichaClinica> obtenerTodas() {
        return repository.findAll();
    }

    public FichaClinica buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("La ficha clínica con ID " + id + " no existe."));
    }

    private FichaClinica mapToEntity(FichaClinicaRequestDTO request) {
        FichaClinica ficha = new FichaClinica();
        ficha.setIdMascota(request.getIdMascota());
        ficha.setObservaciones(request.getObservaciones());
        return ficha;
    }

    private FichaClinicaResponseDTO mapToResponse(FichaClinica ficha) {
        FichaClinicaResponseDTO response = new FichaClinicaResponseDTO();
        response.setIdFicha(ficha.getIdFicha());
        response.setIdMascota(ficha.getIdMascota());
        response.setFechaCreacion(ficha.getFechaCreacion());
        response.setObservaciones(ficha.getObservaciones());
        return response;
    }
}