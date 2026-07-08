package com.vetnova.notificaciones.exception;

import com.vetnova.notificaciones.dto.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        request = mock(HttpServletRequest.class);

        when(request.getRequestURI()).thenReturn("/api/v1/notificaciones");
    }

    @Test
    void manejarJsonInvalido_deberiaRetornarBadRequest() {
        HttpMessageNotReadableException exception =
                new HttpMessageNotReadableException("JSON inválido");

        ResponseEntity<ErrorResponseDTO> response =
                handler.manejarJsonInvalido(exception, request);

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("JSON inválido", response.getBody().getError());
        assertEquals("/api/v1/notificaciones", response.getBody().getPath());
        assertNotNull(response.getBody().getDetails());
        assertEquals(3, response.getBody().getDetails().size());
    }

    @Test
    void manejarNoEncontrado_conRecursoNoEncontrado_deberiaRetornarNotFound() {
        RecursoNoEncontradoException exception =
                new RecursoNoEncontradoException("No existe una notificación");

        ResponseEntity<ErrorResponseDTO> response =
                handler.manejarNoEncontrado(exception, request);

        assertEquals(404, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Recurso no encontrado", response.getBody().getError());
        assertEquals("No existe una notificación", response.getBody().getMessage());
        assertEquals("/api/v1/notificaciones", response.getBody().getPath());
    }

    @Test
    void manejarNoEncontrado_conClienteNoEncontrado_deberiaRetornarNotFound() {
        ClienteNoEncontradoException exception =
                new ClienteNoEncontradoException("Cliente no encontrado");

        ResponseEntity<ErrorResponseDTO> response =
                handler.manejarNoEncontrado(exception, request);

        assertEquals(404, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Recurso no encontrado", response.getBody().getError());
        assertEquals("Cliente no encontrado", response.getBody().getMessage());
    }

    @Test
    void manejarNegocio_deberiaRetornarBadRequest() {
        NegocioException exception =
                new NegocioException("Regla de negocio inválida");

        ResponseEntity<ErrorResponseDTO> response =
                handler.manejarNegocio(exception, request);

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Regla de negocio incumplida", response.getBody().getError());
        assertEquals("Regla de negocio inválida", response.getBody().getMessage());
    }

    @Test
    void manejarMicroservicioNoDisponible_deberiaRetornarServiceUnavailable() {
        MicroservicioNoDisponibleException exception =
                new MicroservicioNoDisponibleException("Clientes no disponible");

        ResponseEntity<ErrorResponseDTO> response =
                handler.manejarMicroservicioNoDisponible(exception, request);

        assertEquals(503, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Microservicio no disponible", response.getBody().getError());
        assertEquals("Clientes no disponible", response.getBody().getMessage());
    }

    @Test
    void manejarParametroInvalido_deberiaRetornarBadRequest() {
        MethodArgumentTypeMismatchException exception =
                mock(MethodArgumentTypeMismatchException.class);

        when(exception.getName()).thenReturn("estado");

        ResponseEntity<ErrorResponseDTO> response =
                handler.manejarParametroInvalido(exception, request);

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Parámetro inválido", response.getBody().getError());
        assertEquals("El parámetro enviado no tiene el formato esperado", response.getBody().getMessage());
        assertNotNull(response.getBody().getDetails());
        assertEquals("estado", response.getBody().getDetails().get(0));
    }

    @Test
    void manejarErrorGeneral_deberiaRetornarInternalServerError() {
        Exception exception = new Exception("Error inesperado");

        ResponseEntity<ErrorResponseDTO> response =
                handler.manejarErrorGeneral(exception, request);

        assertEquals(500, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Error interno", response.getBody().getError());
        assertEquals("Ocurrió un error inesperado en el microservicio Notificaciones",
                response.getBody().getMessage());
        assertEquals("Error inesperado", response.getBody().getDetails().get(0));
    }
}
