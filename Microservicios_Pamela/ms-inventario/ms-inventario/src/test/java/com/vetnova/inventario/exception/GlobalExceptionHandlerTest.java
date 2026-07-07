package com.vetnova.inventario.exception;

import com.vetnova.inventario.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
}