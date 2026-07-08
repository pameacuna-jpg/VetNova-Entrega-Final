package com.vetnova.notificaciones.controller;

import com.vetnova.notificaciones.dto.NotificacionRequestDTO;
import com.vetnova.notificaciones.dto.NotificacionResponseDTO;
import com.vetnova.notificaciones.enums.CanalNotificacion;
import com.vetnova.notificaciones.enums.EstadoNotificacion;
import com.vetnova.notificaciones.enums.PrioridadNotificacion;
import com.vetnova.notificaciones.enums.TipoNotificacion;
import com.vetnova.notificaciones.service.NotificacionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificacionControllerTest {

    @Mock
    private NotificacionService notificacionService;

    private NotificacionController notificacionController;
    private NotificacionRequestDTO request;
    private NotificacionResponseDTO response;

    @BeforeEach
    void setUp() {
        notificacionController = new NotificacionController(notificacionService);

        request = new NotificacionRequestDTO();
        request.setIdCliente(1L);
        request.setMensaje("Mensaje de prueba");
        request.setTipo(TipoNotificacion.STOCK_BAJO);
        request.setCanal(CanalNotificacion.EMAIL);
        request.setPrioridad(PrioridadNotificacion.ALTA);

        response = NotificacionResponseDTO.builder()
                .idNotificacion(1L)
                .idCliente(1L)
                .destinatario("cliente@correo.cl")
                .mensaje("Mensaje de prueba")
                .tipo(TipoNotificacion.STOCK_BAJO)
                .estado(EstadoNotificacion.PENDIENTE)
                .canal(CanalNotificacion.EMAIL)
                .prioridad(PrioridadNotificacion.ALTA)
                .build();
    }

    @Test
    void crear_deberiaRetornarCreated() {
        when(notificacionService.crear(request)).thenReturn(response);

        ResponseEntity<NotificacionResponseDTO> resultado = notificacionController.crear(request);

        assertEquals(HttpStatus.CREATED, resultado.getStatusCode());
        assertNotNull(resultado.getBody());
        assertEquals(1L, resultado.getBody().getIdNotificacion());
        assertEquals("Mensaje de prueba", resultado.getBody().getMensaje());

        verify(notificacionService).crear(request);
    }

    @Test
    void listar_deberiaRetornarOkConLista() {
        when(notificacionService.listar()).thenReturn(Arrays.asList(response));

        ResponseEntity<List<NotificacionResponseDTO>> resultado = notificacionController.listar();

        assertEquals(HttpStatus.OK, resultado.getStatusCode());
        assertNotNull(resultado.getBody());
        assertEquals(1, resultado.getBody().size());

        verify(notificacionService).listar();
    }

    @Test
    void buscarPorId_deberiaRetornarOk() {
        when(notificacionService.buscarPorId(1L)).thenReturn(response);

        ResponseEntity<NotificacionResponseDTO> resultado = notificacionController.buscarPorId(1L);

        assertEquals(HttpStatus.OK, resultado.getStatusCode());
        assertNotNull(resultado.getBody());
        assertEquals(1L, resultado.getBody().getIdNotificacion());

        verify(notificacionService).buscarPorId(1L);
    }

    @Test
    void buscarPorCliente_deberiaRetornarOkConLista() {
        when(notificacionService.buscarPorCliente(1L)).thenReturn(Arrays.asList(response));

        ResponseEntity<List<NotificacionResponseDTO>> resultado = notificacionController.buscarPorCliente(1L);

        assertEquals(HttpStatus.OK, resultado.getStatusCode());
        assertNotNull(resultado.getBody());
        assertEquals(1, resultado.getBody().size());
        assertEquals(1L, resultado.getBody().get(0).getIdCliente());

        verify(notificacionService).buscarPorCliente(1L);
    }

    @Test
    void buscarPorEstado_deberiaRetornarOkConLista() {
        when(notificacionService.buscarPorEstado(EstadoNotificacion.PENDIENTE))
                .thenReturn(Arrays.asList(response));

        ResponseEntity<List<NotificacionResponseDTO>> resultado =
                notificacionController.buscarPorEstado(EstadoNotificacion.PENDIENTE);

        assertEquals(HttpStatus.OK, resultado.getStatusCode());
        assertNotNull(resultado.getBody());
        assertEquals(EstadoNotificacion.PENDIENTE, resultado.getBody().get(0).getEstado());

        verify(notificacionService).buscarPorEstado(EstadoNotificacion.PENDIENTE);
    }

    @Test
    void buscarPorTipo_deberiaRetornarOkConLista() {
        when(notificacionService.buscarPorTipo(TipoNotificacion.STOCK_BAJO))
                .thenReturn(Arrays.asList(response));

        ResponseEntity<List<NotificacionResponseDTO>> resultado =
                notificacionController.buscarPorTipo(TipoNotificacion.STOCK_BAJO);

        assertEquals(HttpStatus.OK, resultado.getStatusCode());
        assertNotNull(resultado.getBody());
        assertEquals(TipoNotificacion.STOCK_BAJO, resultado.getBody().get(0).getTipo());

        verify(notificacionService).buscarPorTipo(TipoNotificacion.STOCK_BAJO);
    }

    @Test
    void marcarComoEnviada_deberiaRetornarOk() {
        NotificacionResponseDTO enviada = NotificacionResponseDTO.builder()
                .idNotificacion(1L)
                .idCliente(1L)
                .destinatario("cliente@correo.cl")
                .mensaje("Mensaje de prueba")
                .tipo(TipoNotificacion.STOCK_BAJO)
                .estado(EstadoNotificacion.ENVIADA)
                .canal(CanalNotificacion.EMAIL)
                .prioridad(PrioridadNotificacion.ALTA)
                .build();

        when(notificacionService.marcarComoEnviada(1L)).thenReturn(enviada);

        ResponseEntity<NotificacionResponseDTO> resultado = notificacionController.marcarComoEnviada(1L);

        assertEquals(HttpStatus.OK, resultado.getStatusCode());
        assertNotNull(resultado.getBody());
        assertEquals(EstadoNotificacion.ENVIADA, resultado.getBody().getEstado());

        verify(notificacionService).marcarComoEnviada(1L);
    }

    @Test
    void marcarComoError_deberiaRetornarOk() {
        NotificacionResponseDTO error = NotificacionResponseDTO.builder()
                .idNotificacion(1L)
                .idCliente(1L)
                .destinatario("cliente@correo.cl")
                .mensaje("Mensaje de prueba")
                .tipo(TipoNotificacion.STOCK_BAJO)
                .estado(EstadoNotificacion.ERROR)
                .canal(CanalNotificacion.EMAIL)
                .prioridad(PrioridadNotificacion.ALTA)
                .build();

        when(notificacionService.marcarComoError(1L)).thenReturn(error);

        ResponseEntity<NotificacionResponseDTO> resultado = notificacionController.marcarComoError(1L);

        assertEquals(HttpStatus.OK, resultado.getStatusCode());
        assertNotNull(resultado.getBody());
        assertEquals(EstadoNotificacion.ERROR, resultado.getBody().getEstado());

        verify(notificacionService).marcarComoError(1L);
    }

    @Test
    void eliminar_deberiaRetornarNoContent() {
        doNothing().when(notificacionService).eliminar(1L);

        ResponseEntity<Void> resultado = notificacionController.eliminar(1L);

        assertEquals(HttpStatus.NO_CONTENT, resultado.getStatusCode());
        assertNull(resultado.getBody());

        verify(notificacionService).eliminar(1L);
    }
}
