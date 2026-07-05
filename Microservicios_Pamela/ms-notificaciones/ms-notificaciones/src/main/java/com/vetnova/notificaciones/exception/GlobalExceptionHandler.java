package com.vetnova.notificaciones.exception;

import com.vetnova.notificaciones.dto.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> manejarValidaciones(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        List<String> detalles = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        return construirRespuesta(
                HttpStatus.BAD_REQUEST,
                "Error de validación",
                "Los datos enviados no cumplen las reglas del sistema",
                request.getRequestURI(),
                detalles
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDTO> manejarJsonInvalido(
            HttpMessageNotReadableException ex,
            HttpServletRequest request
    ) {
        return construirRespuesta(
                HttpStatus.BAD_REQUEST,
                "JSON inválido",
                "Revise los valores enviados. Tipo, canal y prioridad deben usar valores permitidos",
                request.getRequestURI(),
                List.of("Tipos permitidos: CITA, VENTA, STOCK_BAJO, ATENCION_CLINICA, SISTEMA",
                        "Canales permitidos: EMAIL, SMS, WHATSAPP",
                        "Prioridades permitidas: BAJA, MEDIA, ALTA")
        );
    }

    @ExceptionHandler({
            RecursoNoEncontradoException.class,
            ClienteNoEncontradoException.class
    })
    public ResponseEntity<ErrorResponseDTO> manejarNoEncontrado(
            RuntimeException ex,
            HttpServletRequest request
    ) {
        return construirRespuesta(
                HttpStatus.NOT_FOUND,
                "Recurso no encontrado",
                ex.getMessage(),
                request.getRequestURI(),
                null
        );
    }

    @ExceptionHandler(NegocioException.class)
    public ResponseEntity<ErrorResponseDTO> manejarNegocio(
            NegocioException ex,
            HttpServletRequest request
    ) {
        return construirRespuesta(
                HttpStatus.BAD_REQUEST,
                "Regla de negocio incumplida",
                ex.getMessage(),
                request.getRequestURI(),
                null
        );
    }

    @ExceptionHandler(MicroservicioNoDisponibleException.class)
    public ResponseEntity<ErrorResponseDTO> manejarMicroservicioNoDisponible(
            MicroservicioNoDisponibleException ex,
            HttpServletRequest request
    ) {
        return construirRespuesta(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Microservicio no disponible",
                ex.getMessage(),
                request.getRequestURI(),
                null
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDTO> manejarParametroInvalido(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request
    ) {
        return construirRespuesta(
                HttpStatus.BAD_REQUEST,
                "Parámetro inválido",
                "El parámetro enviado no tiene el formato esperado",
                request.getRequestURI(),
                List.of(ex.getName())
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> manejarErrorGeneral(
            Exception ex,
            HttpServletRequest request
    ) {
        return construirRespuesta(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Error interno",
                "Ocurrió un error inesperado en el microservicio Notificaciones",
                request.getRequestURI(),
                List.of(ex.getMessage())
        );
    }

    private ResponseEntity<ErrorResponseDTO> construirRespuesta(
            HttpStatus status,
            String error,
            String message,
            String path,
            List<String> details
    ) {
        ErrorResponseDTO body = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(error)
                .message(message)
                .path(path)
                .details(details)
                .build();

        return ResponseEntity.status(status).body(body);
    }
}