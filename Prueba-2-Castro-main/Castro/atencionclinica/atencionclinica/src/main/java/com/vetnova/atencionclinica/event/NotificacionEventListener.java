package com.vetnova.atencionclinica.event;

import com.vetnova.atencionclinica.dto.NotificacionRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificacionEventListener {

    private final RestTemplate restTemplate;

    @Value("${notificaciones.url}")
    private String notificacionesUrl;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleRecetaEmitida(RecetaEmitidaEvent event) {
        try {
            NotificacionRequest request = new NotificacionRequest(
                    event.getIdCliente(),
                    "Se emitió una receta médica para la mascota ID: " + event.getIdMascota(),
                    "ATENCION_CLINICA",
                    "EMAIL",
                    "ALTA"
            );
            restTemplate.postForEntity(notificacionesUrl, request, String.class);
            log.info("Notificación RECETA_EMITIDA enviada para diagnóstico {}", event.getIdDiagnostico());
        } catch (HttpClientErrorException.NotFound notFound) {
            log.error("Notificaciones respondió 404 para receta emitida: {}", notFound.getMessage());
        } catch (ResourceAccessException ex) {
            log.error("No se puede conectar con el servicio de notificaciones: {}", ex.getMessage());
        } catch (RestClientException ex) {
            log.error("Error al enviar notificación de receta emitida: {}", ex.getMessage());
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleCertificadoEmitido(CertificadoEmitidoEvent event) {
        try {
            NotificacionRequest request = new NotificacionRequest(
                    event.getIdCliente(),
                    "Se emitió un certificado clínico para la mascota ID: " + event.getIdMascota(),
                    "ATENCION_CLINICA",
                    "EMAIL",
                    "MEDIA"
            );
            restTemplate.postForEntity(notificacionesUrl, request, String.class);
            log.info("Notificación CERTIFICADO_EMITIDO enviada para diagnóstico {}", event.getIdDiagnostico());
        } catch (HttpClientErrorException.NotFound notFound) {
            log.error("Notificaciones respondió 404 para certificado emitido: {}", notFound.getMessage());
        } catch (ResourceAccessException ex) {
            log.error("No se puede conectar con el servicio de notificaciones: {}", ex.getMessage());
        } catch (RestClientException ex) {
            log.error("Error al enviar notificación de certificado emitido: {}", ex.getMessage());
        }
    }
}
