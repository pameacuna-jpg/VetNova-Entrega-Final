package com.vetnova.auth.exception;

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
    void manejarRuntimeException_debeRetornarBadRequest() {

        RuntimeException ex =
                new RuntimeException("Credenciales inválidas");

        ResponseEntity<String> response =
                handler.manejarRuntimeException(ex);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Credenciales inválidas", response.getBody());
    }

    @Test
    void manejarException_debeRetornarInternalServerError() {

        Exception ex =
                new Exception("Error inesperado");

        ResponseEntity<String> response =
                handler.manejarException(ex);

        assertEquals(500, response.getStatusCode().value());
        assertEquals("Error interno del servidor", response.getBody());
    }
}