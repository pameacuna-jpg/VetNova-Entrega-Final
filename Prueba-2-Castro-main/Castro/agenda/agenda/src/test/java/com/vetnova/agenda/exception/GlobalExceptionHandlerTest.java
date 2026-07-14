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

    @Test
    void shouldHandleBusinessException() {
        BusinessException exception = new BusinessException("La cita ya está CANCELADA");

        ResponseEntity<Map<String, Object>> response = handler.handleBusinessException(exception);
        Map<String, Object> body = response.getBody();

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(body, "El body no debe ser nulo");
        assertEquals("La cita ya está CANCELADA", body.get("message"));
        assertEquals(HttpStatus.CONFLICT.value(), body.get("status"));
        assertEquals("Conflicto de Negocio", body.get("error"));
    }

    @Test
    void shouldHandleValidationErrors() {
        org.springframework.validation.BindingResult bindingResult =
                org.mockito.Mockito.mock(org.springframework.validation.BindingResult.class);
        org.springframework.validation.FieldError fieldError =
                new org.springframework.validation.FieldError("citaRequestDTO", "idCliente", "El ID del cliente es obligatorio");
        org.mockito.Mockito.when(bindingResult.getFieldErrors()).thenReturn(java.util.List.of(fieldError));

        org.springframework.web.bind.MethodArgumentNotValidException exception =
                org.mockito.Mockito.mock(org.springframework.web.bind.MethodArgumentNotValidException.class);
        org.mockito.Mockito.when(exception.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<Map<String, String>> response = handler.handleValidationErrors(exception);
        Map<String, String> body = response.getBody();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(body);
        assertEquals("El ID del cliente es obligatorio", body.get("idCliente"));
    }
}
