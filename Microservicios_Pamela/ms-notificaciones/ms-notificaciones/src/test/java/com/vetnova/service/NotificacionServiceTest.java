package com.vetnova.notificaciones.service;

import com.vetnova.notificaciones.dto.NotificacionRequestDTO;
import com.vetnova.notificaciones.dto.NotificacionResponseDTO;
import com.vetnova.notificaciones.model.Notificacion;
import com.vetnova.notificaciones.repository.NotificacionRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificacionServiceTest {

    @Mock
    private NotificacionRepository notificacionRepository;

    @InjectMocks
    private NotificacionService notificacionService;

    private Notificacion notificacion;
    private NotificacionRequestDTO requestDTO;

    @BeforeEach
    void setUp() {

        notificacion = new Notificacion(
                1L,
                "Administrador",
                "Stock bajo de Vacuna Rabia",
                "STOCK_BAJO",
                "PENDIENTE",
                "EMAIL",
                "MEDIA"
        );

        requestDTO = new NotificacionRequestDTO();
        requestDTO.setDestinatario("Administrador");
        requestDTO.setMensaje("Stock bajo de Vacuna Rabia");
        requestDTO.setTipo("STOCK_BAJO");
        requestDTO.setCanal("EMAIL");
        requestDTO.setPrioridad("MEDIA");
    }

    @Test
    void listarNotificaciones_deberiaRetornarLista() {

        when(notificacionRepository.findAll())
                .thenReturn(Arrays.asList(notificacion));

        List<NotificacionResponseDTO> resultado =
                notificacionService.listarNotificaciones();

        assertEquals(1, resultado.size());

        verify(notificacionRepository).findAll();
    }

    @Test
    void buscarPorId_deberiaRetornarNotificacion() {

        when(notificacionRepository.findById(1L))
                .thenReturn(Optional.of(notificacion));

        NotificacionResponseDTO resultado =
                notificacionService.buscarPorId(1L);

        assertEquals("Administrador",
                resultado.getDestinatario());

        verify(notificacionRepository).findById(1L);
    }

    @Test
    void buscarPorId_noExiste_deberiaLanzarExcepcion() {

        when(notificacionRepository.findById(99L))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> notificacionService.buscarPorId(99L)
        );

        assertEquals(
                "Notificación no encontrada con ID: 99",
                exception.getMessage()
        );
    }

    @Test
    void crearNotificacion_deberiaGuardarNotificacion() {

        when(notificacionRepository.save(any(Notificacion.class)))
                .thenReturn(notificacion);

        NotificacionResponseDTO resultado =
                notificacionService.crearNotificacion(requestDTO);

        assertNotNull(resultado);

        assertEquals(
                "PENDIENTE",
                resultado.getEstado()
        );

        verify(notificacionRepository)
                .save(any(Notificacion.class));
    }

    @Test
    void actualizarNotificacion_deberiaModificarDatos() {

        NotificacionRequestDTO nueva = new NotificacionRequestDTO();

        nueva.setDestinatario("Cliente");
        nueva.setMensaje("Recordatorio de vacuna");
        nueva.setTipo("RECORDATORIO");
        nueva.setCanal("SMS");
        nueva.setPrioridad("ALTA");

        when(notificacionRepository.findById(1L))
                .thenReturn(Optional.of(notificacion));

        when(notificacionRepository.save(any(Notificacion.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        NotificacionResponseDTO resultado =
                notificacionService.actualizarNotificacion(1L, nueva);

        assertEquals(
                "Cliente",
                resultado.getDestinatario()
        );

        assertEquals(
                "ALTA",
                resultado.getPrioridad()
        );

        verify(notificacionRepository).save(notificacion);
    }

    @Test
    void marcarEnviada_deberiaCambiarEstado() {

        when(notificacionRepository.findById(1L))
                .thenReturn(Optional.of(notificacion));

        when(notificacionRepository.save(any(Notificacion.class)))
                .thenReturn(notificacion);

        notificacionService.marcarEnviada(1L);

        assertEquals(
                "ENVIADA",
                notificacion.getEstado()
        );

        verify(notificacionRepository).save(notificacion);
    }

    @Test
    void buscarPorEstado_deberiaRetornarResultados() {

        when(notificacionRepository.findByEstadoIgnoreCase("PENDIENTE"))
                .thenReturn(Arrays.asList(notificacion));

        List<NotificacionResponseDTO> resultado =
                notificacionService.buscarPorEstado("PENDIENTE");

        assertEquals(1, resultado.size());
    }

    @Test
    void buscarPorTipo_deberiaRetornarResultados() {

        when(notificacionRepository.findByTipoIgnoreCase("STOCK_BAJO"))
                .thenReturn(Arrays.asList(notificacion));

        List<NotificacionResponseDTO> resultado =
                notificacionService.buscarPorTipo("STOCK_BAJO");

        assertEquals(1, resultado.size());
    }

    @Test
    void buscarPorPrioridad_deberiaRetornarResultados() {

        when(notificacionRepository.findByPrioridadIgnoreCase("MEDIA"))
                .thenReturn(Arrays.asList(notificacion));

        List<NotificacionResponseDTO> resultado =
                notificacionService.buscarPorPrioridad("MEDIA");

        assertEquals(1, resultado.size());
    }
}