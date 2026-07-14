package com.vetnova.agenda.config;

import com.vetnova.agenda.event.CitaAgendadaEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.ResourceAccessException;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class CitaEventListenerTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CitaEventListener listener;

    private LocalDateTime fechaHora;

    @BeforeEach
    void setUp() {
        fechaHora = LocalDateTime.now().plusDays(2);
    }

    @SuppressWarnings("unchecked")
    @Test
    void handleCitaAgendada_estadoAgendada_debeEnviarMensajeDeAgendamiento() {
        CitaAgendadaEvent event = new CitaAgendadaEvent(this, 1L, 10L, 20L, fechaHora, "AGENDADA");

        listener.handleCitaAgendada(event);

        ArgumentCaptor<Map> captor = ArgumentCaptor.forClass(Map.class);
        verify(restTemplate).postForEntity(eq("http://localhost:8089/api/v1/notificaciones"), captor.capture(), eq(Object.class));

        Map<String, Object> body = captor.getValue();
        assertEquals("CLIENTE", body.get("destino"));
        assertEquals(10L, body.get("idCliente"));
        assertEquals("CITA", body.get("tipo"));
        assertEquals("EMAIL", body.get("canal"));
        assertEquals("MEDIA", body.get("prioridad"));
        assertEquals("Su cita para la mascota ID 20 quedó agendada para: " + fechaHora, body.get("mensaje"));
    }

    @SuppressWarnings("unchecked")
    @Test
    void handleCitaAgendada_estadoReprogramada_debeArmarMensajeDeReprogramacion() {
        CitaAgendadaEvent event = new CitaAgendadaEvent(this, 1L, 10L, 20L, fechaHora, "REPROGRAMADA");

        listener.handleCitaAgendada(event);

        ArgumentCaptor<Map> captor = ArgumentCaptor.forClass(Map.class);
        verify(restTemplate).postForEntity(anyString(), captor.capture(), eq(Object.class));

        assertEquals("Su cita para la mascota ID 20 fue reprogramada para: " + fechaHora, captor.getValue().get("mensaje"));
    }

    @SuppressWarnings("unchecked")
    @Test
    void handleCitaAgendada_estadoCancelada_debeArmarMensajeDeCancelacion() {
        CitaAgendadaEvent event = new CitaAgendadaEvent(this, 1L, 10L, 20L, fechaHora, "CANCELADA");

        listener.handleCitaAgendada(event);

        ArgumentCaptor<Map> captor = ArgumentCaptor.forClass(Map.class);
        verify(restTemplate).postForEntity(anyString(), captor.capture(), eq(Object.class));

        assertEquals("Su cita para la mascota ID 20 fue cancelada.", captor.getValue().get("mensaje"));
    }

    @SuppressWarnings("unchecked")
    @Test
    void handleCitaAgendada_estadoConfirmada_debeArmarMensajeDeConfirmacion() {
        CitaAgendadaEvent event = new CitaAgendadaEvent(this, 1L, 10L, 20L, fechaHora, "CONFIRMADA");

        listener.handleCitaAgendada(event);

        ArgumentCaptor<Map> captor = ArgumentCaptor.forClass(Map.class);
        verify(restTemplate).postForEntity(anyString(), captor.capture(), eq(Object.class));

        assertEquals("Su asistencia para la cita de la mascota ID 20 quedó confirmada para: " + fechaHora, captor.getValue().get("mensaje"));
    }

    @Test
    void handleCitaAgendada_siNotificacionesFalla_noDebeLanzarExcepcion() {
        CitaAgendadaEvent event = new CitaAgendadaEvent(this, 1L, 10L, 20L, fechaHora);

        when(restTemplate.postForEntity(anyString(), any(), eq(Object.class)))
                .thenThrow(new ResourceAccessException("Servicio de notificaciones caído"));

        assertDoesNotThrow(() -> listener.handleCitaAgendada(event));
    }
}
