package com.vetnova.mascotas.exception;

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
                new ResourceNotFoundException("Mascota no encontrada");

        ResponseEntity<ErrorResponse> response =
                handler.handleResourceNotFound(ex);

        assertEquals(404, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Mascota no encontrada",
                response.getBody().getMessage());
    }

    @Test
    void handleIllegalArgument_debeRetornar400() {

        IllegalArgumentException ex =
                new IllegalArgumentException("Transferencia inválida");

        ResponseEntity<ErrorResponse> response =
                handler.handleIllegalArgument(ex);

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Transferencia inválida",
                response.getBody().getMessage());
    }
}