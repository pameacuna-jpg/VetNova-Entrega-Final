package com.vetnova.agenda.service;

import com.vetnova.agenda.dto.NotificacionRequest;
import com.vetnova.agenda.event.EventoDominio;
import com.vetnova.agenda.exception.ResourceNotFoundException;
import com.vetnova.agenda.model.Cita;
import com.vetnova.agenda.repository.CitaRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Transactional
public class CitaService {

    private final CitaRepository citaRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final RestTemplate restTemplate;

    @Value("${notificaciones.url}")
    private String notificacionesUrl;

    public CitaService(CitaRepository citaRepository,
                       ApplicationEventPublisher eventPublisher,
                       RestTemplate restTemplate) {
        this.citaRepository = citaRepository;
        this.eventPublisher = eventPublisher;
        this.restTemplate = restTemplate;
    }

    public Cita agendarHora(Cita cita) {
        log.info("Iniciando registro de cita");

        cita.setEstado("AGENDADA");
        Cita nuevaCita = citaRepository.save(cita);

        log.info("Cita guardada con ID: {}", nuevaCita.getId());

        enviarNotificacionCitaAgendada(nuevaCita);

        Map<String, Object> payload = new HashMap<>();
        payload.put("idCita", nuevaCita.getId());
        payload.put("idCliente", nuevaCita.getIdCliente());
        payload.put("idMascota", nuevaCita.getIdMascota());
        payload.put("fecha", nuevaCita.getFechaHora().toLocalDate().toString());
        payload.put("hora", nuevaCita.getFechaHora().toLocalTime().toString());
        payload.put("estado", "AGENDADA");

        EventoDominio<Map<String, Object>> evento = new EventoDominio<>(
                "CitaAgendada",
                "ms-agenda",
                payload
        );

        eventPublisher.publishEvent(evento);

        log.info("Evento CitaAgendada emitido");

        return nuevaCita;
    }

    private void enviarNotificacionCitaAgendada(Cita cita) {
        try {
            NotificacionRequest request = new NotificacionRequest(
                    "cliente-" + cita.getIdCliente(),
                    "Su cita veterinaria fue agendada para el " +
                            cita.getFechaHora().toLocalDate() +
                            " a las " +
                            cita.getFechaHora().toLocalTime(),
                    "CITA_AGENDADA",
                    "EMAIL",
                    "ALTA"
            );

            restTemplate.postForEntity(
                    notificacionesUrl,
                    request,
                    String.class
            );

            log.info("Notificación enviada al MS Notificaciones para la cita ID: {}", cita.getId());

        } catch (Exception e) {
            log.warn("La cita fue agendada, pero no se pudo enviar la notificación: {}", e.getMessage());
        }
    }

    public Cita reprogramarHora(Long id, LocalDateTime nuevaFecha) {
        Cita cita = obtenerCitaPorId(id);
        cita.setFechaHora(nuevaFecha);
        cita.setEstado("REPROGRAMADA");
        return citaRepository.save(cita);
    }

    public Cita cancelarHora(Long id) {
        Cita cita = obtenerCitaPorId(id);
        cita.setEstado("CANCELADA");
        return citaRepository.save(cita);
    }

    public Cita confirmarAsistencia(Long id) {
        Cita cita = obtenerCitaPorId(id);
        cita.setEstado("CONFIRMADA");
        return citaRepository.save(cita);
    }

    public List<Cita> obtenerTodasLasCitas() {
        return citaRepository.findAll();
    }

    public Cita obtenerCitaPorId(Long id) {
        return citaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada con ID: " + id));
    }

    public List<Cita> obtenerCitasProximas24h() {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime manana = ahora.plusDays(1);

        return citaRepository.findAll().stream()
                .filter(cita -> cita.getFechaHora().isAfter(ahora)
                        && cita.getFechaHora().isBefore(manana))
                .toList();
    }
}