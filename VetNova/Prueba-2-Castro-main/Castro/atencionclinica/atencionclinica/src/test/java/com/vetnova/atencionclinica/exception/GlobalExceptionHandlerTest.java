package com.vetnova.atencionclinica.exception;

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
    void handleResourceNotFound_debeRetornar404() {
        ResourceNotFoundException ex =
                new ResourceNotFoundException("Atención no encontrada");

        ResponseEntity<ErrorResponse> response =
                handler.handleResourceNotFound(ex);

        assertEquals(404, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("Atención no encontrada", response.getBody().getMessage());
    }

    @Test
    void handleGlobalException_debeRetornar500() {
        Exception ex = new Exception("Error inesperado");

        ResponseEntity<ErrorResponse> response =
                handler.handleGlobalException(ex);

        assertEquals(500, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().getStatus());
        assertEquals("Error interno en el servidor de Atención Clínica",
                response.getBody().getMessage());
    }
}