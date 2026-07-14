package com.vetnova.inventario.exception;

import com.vetnova.inventario.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void manejarRuntimeException_debeRetornarBadRequest() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/v1/movimientos");

        RuntimeException ex = new RuntimeException("Stock insuficiente para realizar la salida");

        ResponseEntity<ErrorResponse> response =
                handler.manejarRuntimeException(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Error en la solicitud", response.getBody().getError());
        assertEquals("Stock insuficiente para realizar la salida", response.getBody().getMessage());
        assertEquals("/api/v1/movimientos", response.getBody().getPath());
    }

    @Test
    void manejarRuntimeException_debeRetornarNotFoundCuandoMensajeDiceNoEncontrado() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/v1/productos/99");

        RuntimeException ex = new RuntimeException("Producto no encontrado con ID: 99");

        ResponseEntity<ErrorResponse> response =
                handler.manejarRuntimeException(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("Recurso no encontrado", response.getBody().getError());
        assertEquals("Producto no encontrado con ID: 99", response.getBody().getMessage());
    }

    @Test
    void manejarSucursalNoEncontrada_debeRetornarNotFound() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/v1/movimientos");

        SucursalNoEncontradaException ex =
                new SucursalNoEncontradaException("La sucursal indicada no existe");

        ResponseEntity<ErrorResponse> response =
                handler.manejarSucursalNoEncontrada(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("Sucursal no encontrada", response.getBody().getError());
        assertEquals("La sucursal indicada no existe", response.getBody().getMessage());
    }

    @Test
    void manejarMicroservicioNoDisponible_debeRetornarServiceUnavailable() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/v1/movimientos");

        MicroservicioNoDisponibleException ex =
                new MicroservicioNoDisponibleException("El microservicio Sucursales no está disponible");

        ResponseEntity<ErrorResponse> response =
                handler.manejarMicroservicioNoDisponible(ex, request);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(503, response.getBody().getStatus());
        assertEquals("Microservicio no disponible", response.getBody().getError());
    }

    @Test
    void manejarResourceAccess_debeRetornarServiceUnavailable() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/v1/movimientos");

        org.springframework.web.client.ResourceAccessException ex =
                new org.springframework.web.client.ResourceAccessException("Connection refused");

        ResponseEntity<ErrorResponse> response =
                handler.manejarResourceAccess(ex, request);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(503, response.getBody().getStatus());
        assertEquals("Microservicio no disponible", response.getBody().getError());
        assertEquals("No fue posible comunicarse con otro microservicio", response.getBody().getMessage());
    }

    @Test
    void manejarValidaciones_debeRetornarBadRequestConMensajesConcatenados() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/v1/productos");

        org.springframework.validation.BindingResult bindingResult =
                mock(org.springframework.validation.BindingResult.class);
        org.springframework.validation.FieldError fieldError =
                new org.springframework.validation.FieldError("productoRequestDTO", "nombre", "El nombre es obligatorio");
        when(bindingResult.getFieldErrors()).thenReturn(java.util.List.of(fieldError));

        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<ErrorResponse> response =
                handler.manejarValidaciones(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("nombre: El nombre es obligatorio", response.getBody().getMessage());
    }

    @Test
    void manejarExceptionGeneral_debeRetornarInternalServerError() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/v1/productos");

        Exception ex = new Exception("Fallo inesperado");

        ResponseEntity<ErrorResponse> response =
                handler.manejarExceptionGeneral(ex, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().getStatus());
        assertEquals("Ocurrió un error inesperado en Inventario: Fallo inesperado", response.getBody().getMessage());
    }

    @Test
    void manejarRuntimeException_debeRetornarBadRequestCuandoMensajeDiceTipoInvalido() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/v1/movimientos");

        RuntimeException ex = new RuntimeException("Tipo de movimiento inválido");

        ResponseEntity<ErrorResponse> response =
                handler.manejarRuntimeException(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
    }
}