package com.vetnova.agenda.exception;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void shouldHandleServiceUnavailableException() {
        ServiceUnavailableException exception = new ServiceUnavailableException("Servicio no disponible");

        ResponseEntity<Map<String, Object>> response = handler.handleServiceUnavailable(exception);
        Map<String, Object> body = response.getBody();

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(body, "El body no debe ser nulo");
        Object message = body != null ? body.get("message") : null;
        Object status = body != null ? body.get("status") : null;
        assertEquals("Servicio no disponible", message, "El mensaje debe estar presente");
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE.value(), status, "El estado debe estar presente");
    }

    @Test
    void shouldHandleResourceNotFoundException() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Recurso no encontrado");

        ResponseEntity<Map<String, Object>> response = handler.handleResourceNotFound(exception);
        Map<String, Object> body = response.getBody();

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(body, "El body no debe ser nulo");
        Object message = body != null ? body.get("message") : null;
        Object status = body != null ? body.get("status") : null;
        assertEquals("Recurso no encontrado", message, "El mensaje debe estar presente");
        assertEquals(HttpStatus.NOT_FOUND.value(), status, "El estado debe estar presente");
    }
}
