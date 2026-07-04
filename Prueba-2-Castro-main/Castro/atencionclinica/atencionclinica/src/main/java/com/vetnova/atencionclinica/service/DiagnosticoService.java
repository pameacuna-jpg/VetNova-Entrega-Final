package com.vetnova.atencionclinica.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vetnova.atencionclinica.dto.DiagnosticoRequestDTO;
import com.vetnova.atencionclinica.dto.DiagnosticoResponseDTO;
import com.vetnova.atencionclinica.event.CertificadoEmitidoEvent;
import com.vetnova.atencionclinica.event.RecetaEmitidaEvent;
import com.vetnova.atencionclinica.exception.ResourceNotFoundException;
import com.vetnova.atencionclinica.model.Diagnostico;
import com.vetnova.atencionclinica.model.FichaClinica;
import com.vetnova.atencionclinica.repository.DiagnosticoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DiagnosticoService {

    private static final Logger logger = LoggerFactory.getLogger(DiagnosticoService.class);

    private final DiagnosticoRepository repository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional(readOnly = true)
    public DiagnosticoResponseDTO buscarPorId(Long id) {
        Diagnostico diagnostico = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("La atención con ID " + id + " no existe."));
        return mapToResponse(diagnostico);
    }

    @Transactional
    public DiagnosticoResponseDTO registrarDiagnostico(DiagnosticoRequestDTO request) {
        Diagnostico diagnostico = mapToEntity(request);
        Diagnostico diagnosticoGuardado = repository.save(diagnostico);

        logger.info("Diagnóstico registrado correctamente. ID: {}",
                diagnosticoGuardado.getIdDiagnostico());

        return mapToResponse(diagnosticoGuardado);
    }

    @Transactional
    public DiagnosticoResponseDTO registrarTratamiento(Long id, String tratamiento) {
        Diagnostico atencion = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("La atención con ID " + id + " no existe."));

        atencion.setTratamiento(tratamiento);
        Diagnostico actualizado = repository.save(atencion);

        logger.info("Tratamiento registrado para diagnóstico ID: {}",
                actualizado.getIdDiagnostico());

        return mapToResponse(actualizado);
    }

    @Transactional
    public DiagnosticoResponseDTO emitirReceta(Long id, String receta) {
        Diagnostico atencion = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("La atención con ID " + id + " no existe."));

        atencion.setRecetaMedica(receta);
        Diagnostico actualizado = repository.save(atencion);

        Long idMascota = obtenerIdMascota(actualizado);
        RecetaEmitidaEvent evento = new RecetaEmitidaEvent(
                actualizado.getIdDiagnostico(),
                actualizado.getIdVeterinario(),
                idMascota,
                actualizado.getRecetaMedica()
        );
        eventPublisher.publishEvent(evento);

        logger.info("Evento RecetaEmitida publicado. Diagnóstico ID: {}, Mascota ID: {}",
                actualizado.getIdDiagnostico(),
                idMascota);

        return mapToResponse(actualizado);
    }

    @Transactional
    public DiagnosticoResponseDTO emitirCertificado(Long id, String detalleCertificado) {
        Diagnostico atencion = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("La atención con ID " + id + " no existe."));

        atencion.setDetalleCertificado(detalleCertificado);
        Diagnostico actualizado = repository.save(atencion);

        Long idMascota = obtenerIdMascota(actualizado);
        CertificadoEmitidoEvent evento = new CertificadoEmitidoEvent(
                actualizado.getIdDiagnostico(),
                actualizado.getIdVeterinario(),
                idMascota,
                actualizado.getDetalleCertificado()
        );
        eventPublisher.publishEvent(evento);

        logger.info("Evento CertificadoEmitido publicado. Diagnóstico ID: {}, Mascota ID: {}",
                actualizado.getIdDiagnostico(),
                idMascota);

        return mapToResponse(actualizado);
    }

    private Diagnostico mapToEntity(DiagnosticoRequestDTO request) {
        Diagnostico diagnostico = new Diagnostico();
        diagnostico.setDescripcion(request.getDescripcion());
        diagnostico.setTratamiento(request.getTratamiento());
        diagnostico.setRecetaMedica(request.getRecetaMedica());
        diagnostico.setDetalleCertificado(request.getDetalleCertificado());
        diagnostico.setIdVeterinario(request.getIdVeterinario());

        FichaClinica fichaClinica = new FichaClinica();
        fichaClinica.setIdFicha(request.getIdFicha());
        diagnostico.setFichaClinica(fichaClinica);

        return diagnostico;
    }

    private DiagnosticoResponseDTO mapToResponse(Diagnostico diagnostico) {
        Long idFicha = null;
        Long idMascota = null;

        if (diagnostico.getFichaClinica() != null) {
            idFicha = diagnostico.getFichaClinica().getIdFicha();
            idMascota = diagnostico.getFichaClinica().getIdMascota();
        }

        return DiagnosticoResponseDTO.builder()
                .idDiagnostico(diagnostico.getIdDiagnostico())
                .descripcion(diagnostico.getDescripcion())
                .tratamiento(diagnostico.getTratamiento())
                .recetaMedica(diagnostico.getRecetaMedica())
                .detalleCertificado(diagnostico.getDetalleCertificado())
                .fecha(diagnostico.getFecha())
                .idVeterinario(diagnostico.getIdVeterinario())
                .idFicha(idFicha)
                .idMascota(idMascota)
                .build();
    }

    private Long obtenerIdMascota(Diagnostico diagnostico) {
        if (diagnostico.getFichaClinica() != null) {
            return diagnostico.getFichaClinica().getIdMascota();
        }
        return null;
    }
}