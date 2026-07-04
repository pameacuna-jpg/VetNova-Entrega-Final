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
            // Armamos dinámicamente el destinatario y el mensaje usando los datos del evento
            Map<String, String> request = Map.of(
                "destinatario", "cliente_id_" + event.getIdCliente() + "@vetnova.cl",
                "mensaje", "Su cita para la mascota ID " + event.getIdMascota() + " quedó agendada para: " + event.getFechaHora()
            );
            
            restTemplate.postForEntity(urlNotificaciones, request, Object.class);
            log.info("Notificación enviada con éxito al ms-notificaciones.");
        } catch (RestClientException e) {
            log.error("Servicio de notificaciones fuera de línea. La cita se guardó, pero la alerta falló.");
        }
    }
}