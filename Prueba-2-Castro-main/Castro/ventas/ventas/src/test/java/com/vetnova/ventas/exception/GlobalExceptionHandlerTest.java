package com.vetnova.ventas.exception;

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
                new ResourceNotFoundException("Venta no encontrada");

        ResponseEntity<ErrorResponse> response =
                handler.handleResourceNotFound(ex);

        assertEquals(404, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().getStatus());
    }

    @Test
    void handleValidationExceptions_debeRetornar400ConPrimerMensaje() {
        org.springframework.validation.BindingResult bindingResult =
                org.mockito.Mockito.mock(org.springframework.validation.BindingResult.class);
        org.springframework.validation.FieldError fieldError =
                new org.springframework.validation.FieldError("ventaRequestDTO", "idProducto", "El ID del producto es obligatorio");
        org.mockito.Mockito.when(bindingResult.getFieldErrors()).thenReturn(java.util.List.of(fieldError));

        org.springframework.web.bind.MethodArgumentNotValidException ex =
                org.mockito.Mockito.mock(org.springframework.web.bind.MethodArgumentNotValidException.class);
        org.mockito.Mockito.when(ex.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<ErrorResponse> response = handler.handleValidationExceptions(ex);

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("El ID del producto es obligatorio", response.getBody().getMessage());
    }

    @Test
    void handleBusinessException_debeRetornar409() {
        BusinessException ex = new BusinessException("Stock insuficiente");

        ResponseEntity<ErrorResponse> response = handler.handleBusinessException(ex);

        assertEquals(409, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(409, response.getBody().getStatus());
        assertEquals("Stock insuficiente", response.getBody().getMessage());
    }

    @Test
    void handleServiceUnavailable_debeRetornar503() {
        ServiceUnavailableException ex = new ServiceUnavailableException("Inventario no disponible");

        ResponseEntity<ErrorResponse> response = handler.handleServiceUnavailable(ex);

        assertEquals(503, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(503, response.getBody().getStatus());
        assertEquals("Inventario no disponible", response.getBody().getMessage());
    }

    @Test
    void handleGlobalException_debeRetornar500() {

        Exception ex = new Exception("Error inesperado");

        ResponseEntity<ErrorResponse> response =
                handler.handleGlobalException(ex);

        assertEquals(500, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().getStatus());
    }
}