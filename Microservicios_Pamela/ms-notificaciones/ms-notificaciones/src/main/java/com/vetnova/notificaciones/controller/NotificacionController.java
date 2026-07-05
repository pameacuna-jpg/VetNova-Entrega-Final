package com.vetnova.notificaciones.controller;

import com.vetnova.notificaciones.dto.NotificacionRequestDTO;
import com.vetnova.notificaciones.dto.NotificacionResponseDTO;
import com.vetnova.notificaciones.enums.EstadoNotificacion;
import com.vetnova.notificaciones.enums.TipoNotificacion;
import com.vetnova.notificaciones.service.NotificacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notificaciones")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class NotificacionController {

    private final NotificacionService notificacionService;

    @PostMapping
    public ResponseEntity<NotificacionResponseDTO> crear(
            @Valid @RequestBody NotificacionRequestDTO request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(notificacionService.crear(request));
    }

    @GetMapping
    public ResponseEntity<List<NotificacionResponseDTO>> listar() {
        return ResponseEntity.ok(notificacionService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificacionResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(notificacionService.buscarPorId(id));
    }

    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<NotificacionResponseDTO>> buscarPorCliente(
            @PathVariable Long idCliente
    ) {
        return ResponseEntity.ok(notificacionService.buscarPorCliente(idCliente));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<NotificacionResponseDTO>> buscarPorEstado(
            @PathVariable EstadoNotificacion estado
    ) {
        return ResponseEntity.ok(notificacionService.buscarPorEstado(estado));
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<NotificacionResponseDTO>> buscarPorTipo(
            @PathVariable TipoNotificacion tipo
    ) {
        return ResponseEntity.ok(notificacionService.buscarPorTipo(tipo));
    }

    @PatchMapping("/{id}/enviada")
    public ResponseEntity<NotificacionResponseDTO> marcarComoEnviada(@PathVariable Long id) {
        return ResponseEntity.ok(notificacionService.marcarComoEnviada(id));
    }

    @PatchMapping("/{id}/error")
    public ResponseEntity<NotificacionResponseDTO> marcarComoError(@PathVariable Long id) {
        return ResponseEntity.ok(notificacionService.marcarComoError(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        notificacionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}