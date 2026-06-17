package com.vetnova.notificaciones.controller;

import com.vetnova.notificaciones.dto.NotificacionRequestDTO;
import com.vetnova.notificaciones.dto.NotificacionResponseDTO;
import com.vetnova.notificaciones.service.NotificacionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notificaciones")
public class NotificacionController {

    // 1. Declarar la dependencia como final (Buenas prácticas de diseño)
    private final NotificacionService notificacionService;

    // 2. Inyección explícita a través del constructor
    public NotificacionController(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    @GetMapping
    public ResponseEntity<List<NotificacionResponseDTO>> listarNotificaciones() {
        return ResponseEntity.ok(notificacionService.listarNotificaciones());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificacionResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(notificacionService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<NotificacionResponseDTO> crearNotificacion(
            @Valid @RequestBody NotificacionRequestDTO notificacionRequestDTO) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(notificacionService.crearNotificacion(notificacionRequestDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NotificacionResponseDTO> actualizarNotificacion(
            @PathVariable Long id,
            @Valid @RequestBody NotificacionRequestDTO notificacionRequestDTO) {

        return ResponseEntity.ok(
                notificacionService.actualizarNotificacion(id, notificacionRequestDTO)
        );
    }

    @PatchMapping("/enviar/{id}")
    public ResponseEntity<Void> marcarEnviada(@PathVariable Long id) {
        notificacionService.marcarEnviada(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<NotificacionResponseDTO>> buscarPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(notificacionService.buscarPorEstado(estado));
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<NotificacionResponseDTO>> buscarPorTipo(@PathVariable String tipo) {
        return ResponseEntity.ok(notificacionService.buscarPorTipo(tipo));
    }

    @GetMapping("/prioridad/{prioridad}")
    public ResponseEntity<List<NotificacionResponseDTO>> buscarPorPrioridad(@PathVariable String prioridad) {
        return ResponseEntity.ok(notificacionService.buscarPorPrioridad(prioridad));
    }
}