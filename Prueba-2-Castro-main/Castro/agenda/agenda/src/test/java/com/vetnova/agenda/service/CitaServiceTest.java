package com.vetnova.agenda.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.vetnova.agenda.dto.CitaRequestDTO;
import com.vetnova.agenda.dto.CitaResponseDTO;
import com.vetnova.agenda.event.CitaAgendadaEvent;
import com.vetnova.agenda.exception.BusinessException;
import com.vetnova.agenda.exception.ResourceNotFoundException;
import com.vetnova.agenda.exception.ServiceUnavailableException;
import com.vetnova.agenda.model.Cita;
import com.vetnova.agenda.repository.CitaRepository;

@ExtendWith(MockitoExtension.class)
class CitaServiceTest {

    @Mock
    private CitaRepository citaRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CitaServiceImpl citaService;

    private Cita cita;
    private CitaRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(citaService, "clientesServiceUrl", "http://localhost:8084");
        ReflectionTestUtils.setField(citaService, "mascotasServiceUrl", "http://localhost:8085");

        cita = new Cita();
        cita.setId(1L);
        cita.setIdCliente(10L);
        cita.setIdMascota(20L);
        cita.setIdVeterinario(30L);
        cita.setFechaHora(LocalDateTime.now().plusDays(1));
        cita.setMotivo("Consulta general");
        cita.setEstado("PENDIENTE");

        requestDTO = new CitaRequestDTO();
        requestDTO.setIdCliente(10L);
        requestDTO.setIdMascota(20L);
        requestDTO.setIdVeterinario(30L);
        requestDTO.setFechaHora(LocalDateTime.now().plusDays(1));
        requestDTO.setMotivo("Consulta general");
    }

    @Test
    void agendarHora_debeCrearCitaYEmitirEventoCuandoTodoSaleBien() {
        when(restTemplate.getForEntity(anyString(), eq(Object.class)))
                .thenReturn(ResponseEntity.ok().build());
        when(citaRepository.save(any(Cita.class))).thenAnswer(invocation -> {
            Cita entidad = invocation.getArgument(0);
            entidad.setId(1L);
            return entidad;
        });

        CitaResponseDTO resultado = citaService.agendarHora(requestDTO);

        assertNotNull(resultado);
        assertEquals("AGENDADA", resultado.getEstado());
        assertEquals(1L, resultado.getId());
        verify(restTemplate, org.mockito.Mockito.times(2)).getForEntity(anyString(), eq(Object.class));
        verify(citaRepository).save(any(Cita.class));
        verify(eventPublisher).publishEvent(any(CitaAgendadaEvent.class));
    }

    @Test
    void agendarHora_siElClienteNoExiste_debeLanzarResourceNotFoundException() {
        when(restTemplate.getForEntity(anyString(), eq(Object.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> citaService.agendarHora(requestDTO)
        );

        assertTrue(exception.getMessage().contains("cliente"));
    }

    @Test
    void agendarHora_siElServicioEstaCaido_debeLanzarServiceUnavailableException() {
        when(restTemplate.getForEntity(anyString(), eq(Object.class)))
                .thenThrow(new ResourceAccessException("Connection refused"));

        ServiceUnavailableException exception = assertThrows(
                ServiceUnavailableException.class,
                () -> citaService.agendarHora(requestDTO)
        );

        assertTrue(exception.getMessage().contains("activo"));
    }

    @Test
    void obtenerCitaPorId_cuandoExiste_debeRetornarCita() {
        when(citaRepository.findById(1L)).thenReturn(Optional.of(cita));

        CitaResponseDTO resultado = citaService.obtenerCitaPorId(1L);

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
        when(citaRepository.findAll()).thenReturn(List.of(cita));
        when(citaRepository.save(any(Cita.class))).thenReturn(cita);

        CitaResponseDTO resultado = citaService.reprogramarHora(1L, nuevaFecha);

        assertNotNull(resultado);
        assertEquals(nuevaFecha, resultado.getFechaHora());
        assertEquals("REPROGRAMADA", resultado.getEstado());

        verify(citaRepository).findById(1L);
        verify(citaRepository).save(cita);
        verify(eventPublisher).publishEvent(any(CitaAgendadaEvent.class));
    }

    @Test
    void reprogramarHora_siHayConflictoDeHorarioConMismoVeterinario_debeLanzarBusinessException() {
        LocalDateTime nuevaFecha = LocalDateTime.now().plusDays(5);

        Cita otraCitaMismoVeterinario = new Cita();
        otraCitaMismoVeterinario.setId(2L);
        otraCitaMismoVeterinario.setIdVeterinario(30L);
        otraCitaMismoVeterinario.setEstado("AGENDADA");
        otraCitaMismoVeterinario.setFechaHora(nuevaFecha.plusMinutes(10));

        when(citaRepository.findById(1L)).thenReturn(Optional.of(cita));
        when(citaRepository.findAll()).thenReturn(List.of(cita, otraCitaMismoVeterinario));

        assertThrows(BusinessException.class, () -> citaService.reprogramarHora(1L, nuevaFecha));

        verify(citaRepository, org.mockito.Mockito.never()).save(any(Cita.class));
    }

    @Test
    void cancelarHora_debeCambiarEstadoACancelada() {
        when(citaRepository.findById(1L)).thenReturn(Optional.of(cita));
        when(citaRepository.save(any(Cita.class))).thenReturn(cita);

        CitaResponseDTO resultado = citaService.cancelarHora(1L);

        assertNotNull(resultado);
        assertEquals("CANCELADA", resultado.getEstado());

        verify(citaRepository).findById(1L);
        verify(citaRepository).save(cita);
        verify(eventPublisher).publishEvent(any(CitaAgendadaEvent.class));
    }

    @Test
    void cancelarHora_siYaEstaCancelada_debeLanzarBusinessException() {
        cita.setEstado("CANCELADA");
        when(citaRepository.findById(1L)).thenReturn(Optional.of(cita));

        assertThrows(BusinessException.class, () -> citaService.cancelarHora(1L));

        verify(citaRepository, org.mockito.Mockito.never()).save(any(Cita.class));
    }

    @Test
    void confirmarAsistencia_debeCambiarEstadoAConfirmada() {
        when(citaRepository.findById(1L)).thenReturn(Optional.of(cita));
        when(citaRepository.save(any(Cita.class))).thenReturn(cita);

        CitaResponseDTO resultado = citaService.confirmarAsistencia(1L);

        assertNotNull(resultado);
        assertEquals("CONFIRMADA", resultado.getEstado());

        verify(citaRepository).findById(1L);
        verify(citaRepository).save(cita);
        verify(eventPublisher).publishEvent(any(CitaAgendadaEvent.class));
    }

    @Test
    void confirmarAsistencia_siYaEstaCancelada_debeLanzarBusinessException() {
        cita.setEstado("CANCELADA");
        when(citaRepository.findById(1L)).thenReturn(Optional.of(cita));

        assertThrows(BusinessException.class, () -> citaService.confirmarAsistencia(1L));

        verify(citaRepository, org.mockito.Mockito.never()).save(any(Cita.class));
    }

    @Test
    void obtenerTodasLasCitas_debeRetornarLista() {
        when(citaRepository.findAll()).thenReturn(List.of(cita));

        List<CitaResponseDTO> resultado = citaService.obtenerTodasLasCitas();

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

        List<CitaResponseDTO> resultado = citaService.obtenerCitasProximas24h();

        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getId());

        verify(citaRepository).findAll();
    }
}