package com.vetnova.notificaciones.controller;

import com.vetnova.notificaciones.model.Notificacion;
import com.vetnova.notificaciones.service.NotificacionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.List;

@RestController
@RequestMapping("/api/v1/notificaciones")
public class NotificacionController {

    @Autowired
    private NotificacionService notificacionService;

    @GetMapping
    public List<Notificacion> listarNotificaciones() {

        return notificacionService.listarNotificaciones();
    }

    @GetMapping("/{id}")
    public Notificacion buscarPorId(@PathVariable Long id) {

        return notificacionService.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<Notificacion> crearNotificacion(@Valid @RequestBody Notificacion notificacion) {
        return ResponseEntity.ok(notificacionService.crearNotificacion(notificacion));
    }


    @PutMapping("/{id}")
    public Notificacion actualizarNotificacion(@PathVariable Long id,
                                               @Valid @RequestBody Notificacion notificacion) {

        return notificacionService.actualizarNotificacion(id, notificacion);
    }

    @PatchMapping("/enviar/{id}")
    public String marcarEnviada(@PathVariable Long id) {

        notificacionService.marcarEnviada(id);

        return "Notificación enviada correctamente";
    }

    @GetMapping("/estado/{estado}")
    public List<Notificacion> buscarPorEstado(@PathVariable String estado) {

        return notificacionService.buscarPorEstado(estado);
    }

    @GetMapping("/tipo/{tipo}")
    public List<Notificacion> buscarPorTipo(@PathVariable String tipo) {

        return notificacionService.buscarPorTipo(tipo);
    }

    @GetMapping("/prioridad/{prioridad}")
    public List<Notificacion> buscarPorPrioridad(@PathVariable String prioridad) {

        return notificacionService.buscarPorPrioridad(prioridad);
    }
}