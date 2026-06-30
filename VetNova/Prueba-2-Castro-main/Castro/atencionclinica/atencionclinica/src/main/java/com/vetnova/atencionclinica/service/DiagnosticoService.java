package com.vetnova.atencionclinica.service;

import com.vetnova.atencionclinica.dto.NotificacionRequest;
import com.vetnova.atencionclinica.event.CertificadoEmitidoEvent;
import com.vetnova.atencionclinica.event.RecetaEmitidaEvent;
import com.vetnova.atencionclinica.exception.ResourceNotFoundException;
import com.vetnova.atencionclinica.model.Diagnostico;
import com.vetnova.atencionclinica.repository.DiagnosticoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
public class DiagnosticoService {

    private static final Logger logger = LoggerFactory.getLogger(DiagnosticoService.class);

    private final DiagnosticoRepository repository;
    private final ApplicationEventPublisher eventPublisher;
    private final RestTemplate restTemplate;

    @Value("${notificaciones.url}")
    private String notificacionesUrl;

    public DiagnosticoService(DiagnosticoRepository repository,
                              ApplicationEventPublisher eventPublisher,
                              RestTemplate restTemplate) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
        this.restTemplate = restTemplate;
    }

    @Transactional(readOnly = true)
    public Diagnostico buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("La atención con ID " + id + " no existe."));
    }

    @Transactional
    public Diagnostico registrarDiagnostico(Diagnostico diagnostico) {
        Diagnostico diagnosticoGuardado = repository.save(diagnostico);

        logger.info("Diagnóstico registrado correctamente. ID: {}",
                diagnosticoGuardado.getIdDiagnostico());

        return diagnosticoGuardado;
    }

    @Transactional
    public Diagnostico registrarTratamiento(Long id, String tratamiento) {
        Diagnostico atencion = buscarPorId(id);
        atencion.setTratamiento(tratamiento);

        Diagnostico actualizado = repository.save(atencion);

        logger.info("Tratamiento registrado para diagnóstico ID: {}",
                actualizado.getIdDiagnostico());

        return actualizado;
    }

    @Transactional
    public Diagnostico emitirReceta(Long id, String receta) {
        Diagnostico atencion = buscarPorId(id);
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

        enviarNotificacionReceta(actualizado, idMascota);

        logger.info("Evento RecetaEmitida publicado. Diagnóstico ID: {}, Mascota ID: {}",
                actualizado.getIdDiagnostico(),
                idMascota);

        return actualizado;
    }

    @Transactional
    public Diagnostico emitirCertificado(Long id, String detalleCertificado) {
        Diagnostico atencion = buscarPorId(id);
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

        enviarNotificacionCertificado(actualizado, idMascota);

        logger.info("Evento CertificadoEmitido publicado. Diagnóstico ID: {}, Mascota ID: {}",
                actualizado.getIdDiagnostico(),
                idMascota);

        return actualizado;
    }

    private Long obtenerIdMascota(Diagnostico diagnostico) {
        if (diagnostico.getFichaClinica() != null) {
            return diagnostico.getFichaClinica().getIdMascota();
        }
        return null;
    }

    private void enviarNotificacionReceta(Diagnostico diagnostico, Long idMascota) {
        try {
            NotificacionRequest request = new NotificacionRequest(
                    "cliente-mascota-" + idMascota,
                    "Se emitió una receta médica para la mascota ID: " + idMascota,
                    "RECETA_EMITIDA",
                    "EMAIL",
                    "ALTA"
            );

            restTemplate.postForEntity(notificacionesUrl, request, String.class);

            logger.info("Notificación RECETA_EMITIDA enviada. Diagnóstico ID: {}",
                    diagnostico.getIdDiagnostico());

        } catch (Exception e) {
            logger.warn("No se pudo enviar notificación RECETA_EMITIDA: {}", e.getMessage());
        }
    }

    private void enviarNotificacionCertificado(Diagnostico diagnostico, Long idMascota) {
        try {
            NotificacionRequest request = new NotificacionRequest(
                    "cliente-mascota-" + idMascota,
                    "Se emitió un certificado clínico para la mascota ID: " + idMascota,
                    "CERTIFICADO_EMITIDO",
                    "EMAIL",
                    "MEDIA"
            );

            restTemplate.postForEntity(notificacionesUrl, request, String.class);

            logger.info("Notificación CERTIFICADO_EMITIDO enviada. Diagnóstico ID: {}",
                    diagnostico.getIdDiagnostico());

        } catch (Exception e) {
            logger.warn("No se pudo enviar notificación CERTIFICADO_EMITIDO: {}", e.getMessage());
        }
    }
}