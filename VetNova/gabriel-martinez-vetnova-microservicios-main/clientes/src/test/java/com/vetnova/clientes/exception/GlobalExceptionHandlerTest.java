package com.vetnova.clientes.exception;

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
                new ResourceNotFoundException("Cliente no encontrado");

        ResponseEntity<ErrorResponse> response =
                handler.handleResourceNotFound(ex);

        assertEquals(404, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("Not Found", response.getBody().getError());
        assertEquals("Cliente no encontrado", response.getBody().getMessage());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void handleIllegalArgument_debeRetornarBadRequest() {
        IllegalArgumentException ex =
                new IllegalArgumentException("RUT ya registrado");

        ResponseEntity<ErrorResponse> response =
                handler.handleIllegalArgument(ex);

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Bad Request", response.getBody().getError());
        assertEquals("RUT ya registrado", response.getBody().getMessage());
        assertNotNull(response.getBody().getTimestamp());
    }
}
