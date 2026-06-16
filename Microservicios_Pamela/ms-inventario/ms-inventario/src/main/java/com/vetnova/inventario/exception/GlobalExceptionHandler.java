package com.vetnova.inventario.exception;

import com.vetnova.inventario.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse manejarRuntimeException(RuntimeException ex, HttpServletRequest request) {

        HttpStatus status = determinarStatus(ex.getMessage());

        return new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status == HttpStatus.NOT_FOUND ? "Recurso no encontrado" : "Error en la solicitud",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse manejarValidaciones(MethodArgumentNotValidException ex, HttpServletRequest request) {

        String mensajes = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Error de validación",
                mensajes,
                request.getRequestURI()
        );
    }

    private HttpStatus determinarStatus(String mensaje) {
        if (mensaje != null && mensaje.toLowerCase().contains("no encontrado")) {
            return HttpStatus.NOT_FOUND;
        }

        return HttpStatus.BAD_REQUEST;
    }
}