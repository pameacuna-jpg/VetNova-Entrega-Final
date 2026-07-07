package com.vetnova.inventario.exception;

import com.vetnova.inventario.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SucursalNoEncontradaException.class)
    public ResponseEntity<ErrorResponse> manejarSucursalNoEncontrada(
            SucursalNoEncontradaException ex,
            HttpServletRequest request
    ) {
        return construirRespuesta(
                HttpStatus.NOT_FOUND,
                "Sucursal no encontrada",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(MicroservicioNoDisponibleException.class)
    public ResponseEntity<ErrorResponse> manejarMicroservicioNoDisponible(
            MicroservicioNoDisponibleException ex,
            HttpServletRequest request
    ) {
        return construirRespuesta(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Microservicio no disponible",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<ErrorResponse> manejarResourceAccess(
            ResourceAccessException ex,
            HttpServletRequest request
    ) {
        return construirRespuesta(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Microservicio no disponible",
                "No fue posible comunicarse con otro microservicio",
                request.getRequestURI()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> manejarValidaciones(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        String mensajes = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return construirRespuesta(
                HttpStatus.BAD_REQUEST,
                "Error de validación",
                mensajes,
                request.getRequestURI()
        );
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> manejarRuntimeException(
            RuntimeException ex,
            HttpServletRequest request
    ) {
        HttpStatus status = determinarStatus(ex.getMessage());

        return construirRespuesta(
                status,
                status == HttpStatus.NOT_FOUND ? "Recurso no encontrado" : "Error en la solicitud",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> manejarExceptionGeneral(
            Exception ex,
            HttpServletRequest request
    ) {
        return construirRespuesta(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Error interno",
                "Ocurrió un error inesperado en Inventario: " + ex.getMessage(),
                request.getRequestURI()
        );
    }

    private ResponseEntity<ErrorResponse> construirRespuesta(
            HttpStatus status,
            String error,
            String mensaje,
            String path
    ) {
        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                error,
                mensaje,
                path
        );

        return ResponseEntity.status(status).body(response);
    }

    private HttpStatus determinarStatus(String mensaje) {
        if (mensaje != null && mensaje.toLowerCase().contains("no encontrado")) {
            return HttpStatus.NOT_FOUND;
        }

        if (mensaje != null && mensaje.toLowerCase().contains("stock insuficiente")) {
            return HttpStatus.BAD_REQUEST;
        }

        if (mensaje != null && mensaje.toLowerCase().contains("tipo de movimiento inválido")) {
            return HttpStatus.BAD_REQUEST;
        }

        return HttpStatus.BAD_REQUEST;
    }
}