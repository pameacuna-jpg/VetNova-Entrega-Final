package com.vetnova.agenda.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleResourceNotFound_debeRetornarNotFound() {
        ResourceNotFoundException ex =
                new ResourceNotFoundException("Cita no encontrada con ID: 99");

        ResponseEntity<ErrorResponse> response =
                handler.handleResourceNotFound(ex);

        assertEquals(404, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("Cita no encontrada con ID: 99", response.getBody().getMessage());
    }

    @Test
    void handleGlobalException_debeRetornarInternalServerError() {
        Exception ex = new Exception("Error inesperado");

        ResponseEntity<ErrorResponse> response =
                handler.handleGlobalException(ex);

        assertEquals(500, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().getStatus());
        assertTrue(response.getBody().getMessage().contains("Error interno en el servidor"));
    }
}