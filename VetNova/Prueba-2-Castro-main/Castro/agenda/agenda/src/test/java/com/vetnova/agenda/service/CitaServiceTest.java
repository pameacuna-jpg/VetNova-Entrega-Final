package com.vetnova.agenda.service;

import com.vetnova.agenda.event.EventoDominio;
import com.vetnova.agenda.exception.ResourceNotFoundException;
import com.vetnova.agenda.model.Cita;
import com.vetnova.agenda.repository.CitaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CitaServiceTest {

    @Mock
    private CitaRepository citaRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CitaService citaService;

    private Cita cita;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(
                citaService,
                "notificacionesUrl",
                "http://localhost:8089/api/v1/notificaciones"
        );

        cita = new Cita();
        cita.setId(1L);
        cita.setIdCliente(10L);
        cita.setIdMascota(20L);
        cita.setIdVeterinario(30L);
        cita.setFechaHora(LocalDateTime.now().plusDays(1));
        cita.setMotivo("Consulta general");
        cita.setEstado("PENDIENTE");
    }

    @Test
    void agendarHora_debeCrearCitaEmitirEventoYEnviarNotificacion() {
        when(citaRepository.save(any(Cita.class))).thenReturn(cita);

        Cita resultado = citaService.agendarHora(cita);

        assertNotNull(resultado);
        assertEquals("AGENDADA", resultado.getEstado());

        verify(citaRepository).save(cita);
        verify(eventPublisher).publishEvent(any(EventoDominio.class));
        verify(restTemplate).postForEntity(
                eq("http://localhost:8089/api/v1/notificaciones"),
                any(),
                eq(String.class)
        );
    }

    @Test
    void agendarHora_siFallaNotificacion_debeMantenerCitaAgendada() {
        when(citaRepository.save(any(Cita.class))).thenReturn(cita);

        doThrow(new RuntimeException("MS Notificaciones no disponible"))
                .when(restTemplate)
                .postForEntity(anyString(), any(), eq(String.class));

        Cita resultado = citaService.agendarHora(cita);

        assertNotNull(resultado);
        assertEquals("AGENDADA", resultado.getEstado());

        verify(citaRepository).save(cita);
        verify(eventPublisher).publishEvent(any(EventoDominio.class));
        verify(restTemplate).postForEntity(anyString(), any(), eq(String.class));
    }

    @Test
    void obtenerCitaPorId_cuandoExiste_debeRetornarCita() {
        when(citaRepository.findById(1L)).thenReturn(Optional.of(cita));

        Cita resultado = citaService.obtenerCitaPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        verify(citaRepository).findById(1L);
    }

    @Test
    void obtenerCitaPorId_cuandoNoExiste_debeLanzarExcepcion() {
        when(citaRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> citaService.obtenerCitaPorId(99L)
        );

        assertEquals("Cita no encontrada con ID: 99", exception.getMessage());
        verify(citaRepository).findById(99L);
    }

    @Test
    void reprogramarHora_debeActualizarFechaYEstado() {
        LocalDateTime nuevaFecha = LocalDateTime.now().plusDays(5);

        when(citaRepository.findById(1L)).thenReturn(Optional.of(cita));
        when(citaRepository.save(any(Cita.class))).thenReturn(cita);

        Cita resultado = citaService.reprogramarHora(1L, nuevaFecha);

        assertNotNull(resultado);
        assertEquals(nuevaFecha, resultado.getFechaHora());
        assertEquals("REPROGRAMADA", resultado.getEstado());

        verify(citaRepository).findById(1L);
        verify(citaRepository).save(cita);
    }

    @Test
    void cancelarHora_debeCambiarEstadoACancelada() {
        when(citaRepository.findById(1L)).thenReturn(Optional.of(cita));
        when(citaRepository.save(any(Cita.class))).thenReturn(cita);

        Cita resultado = citaService.cancelarHora(1L);

        assertNotNull(resultado);
        assertEquals("CANCELADA", resultado.getEstado());

        verify(citaRepository).findById(1L);
        verify(citaRepository).save(cita);
    }

    @Test
    void confirmarAsistencia_debeCambiarEstadoAConfirmada() {
        when(citaRepository.findById(1L)).thenReturn(Optional.of(cita));
        when(citaRepository.save(any(Cita.class))).thenReturn(cita);

        Cita resultado = citaService.confirmarAsistencia(1L);

        assertNotNull(resultado);
        assertEquals("CONFIRMADA", resultado.getEstado());

        verify(citaRepository).findById(1L);
        verify(citaRepository).save(cita);
    }

    @Test
    void obtenerTodasLasCitas_debeRetornarLista() {
        when(citaRepository.findAll()).thenReturn(List.of(cita));

        List<Cita> resultado = citaService.obtenerTodasLasCitas();

        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getId());

        verify(citaRepository).findAll();
    }

    @Test
    void obtenerCitasProximas24h_debeRetornarSoloCitasDentroDelRango() {
        Cita citaDentro = new Cita();
        citaDentro.setId(1L);
        citaDentro.setFechaHora(LocalDateTime.now().plusHours(3));

        Cita citaFuera = new Cita();
        citaFuera.setId(2L);
        citaFuera.setFechaHora(LocalDateTime.now().plusDays(2));

        when(citaRepository.findAll()).thenReturn(List.of(citaDentro, citaFuera));

        List<Cita> resultado = citaService.obtenerCitasProximas24h();

        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getId());

        verify(citaRepository).findAll();
    }
}