package com.vetnova.agenda.config;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.vetnova.agenda.event.CitaAgendadaEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CitaEventListener {

    private final RestTemplate restTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleCitaAgendada(CitaAgendadaEvent event) {
        log.info("Procesando evento asíncrono para Cita ID: {}", event.getIdCita());
        String urlNotificaciones = "http://localhost:8089/api/v1/notificaciones";

        try {
            Map<String, Object> request = Map.of(
                "idCliente", event.getIdCliente(),
                "mensaje", mensajePara(event),
                "tipo", "CITA",
                "canal", "EMAIL",
                "prioridad", "MEDIA"
            );

            restTemplate.postForEntity(urlNotificaciones, request, Object.class);
            log.info("Notificación enviada con éxito al ms-notificaciones.");
        } catch (RestClientException e) {
            log.error("Servicio de notificaciones fuera de línea. La cita se guardó, pero la alerta falló.");
        }
    }

    private String mensajePara(CitaAgendadaEvent event) {
        return switch (event.getEstado()) {
            case "REPROGRAMADA" -> "Su cita para la mascota ID " + event.getIdMascota() + " fue reprogramada para: " + event.getFechaHora();
            case "CANCELADA" -> "Su cita para la mascota ID " + event.getIdMascota() + " fue cancelada.";
            case "CONFIRMADA" -> "Su asistencia para la cita de la mascota ID " + event.getIdMascota() + " quedó confirmada para: " + event.getFechaHora();
            default -> "Su cita para la mascota ID " + event.getIdMascota() + " quedó agendada para: " + event.getFechaHora();
        };
    }
}