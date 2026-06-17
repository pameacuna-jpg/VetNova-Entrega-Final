package com.vetnova.notificaciones.service;

import com.vetnova.notificaciones.dto.NotificacionRequestDTO;
import com.vetnova.notificaciones.dto.NotificacionResponseDTO;
import com.vetnova.notificaciones.model.Notificacion;
import com.vetnova.notificaciones.repository.NotificacionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificacionService {

    private static final Logger logger = 
            LoggerFactory.getLogger(NotificacionService.class);

    // 1. Declarar la dependencia como final (Buenas prácticas de diseño)
    private final NotificacionRepository notificacionRepository;

    // 2. Inyección explícita a través del constructor
    public NotificacionService(NotificacionRepository notificacionRepository) {
        this.notificacionRepository = notificacionRepository;
    }

    public List<NotificacionResponseDTO> listarNotificaciones() {
        return notificacionRepository.findAll()
                .stream()
                .map(this::mapearAResponse)
                .toList();
    }

    public NotificacionResponseDTO buscarPorId(Long id) {
        Notificacion notificacion = obtenerEntidadPorId(id);
        return mapearAResponse(notificacion);
    }

    public NotificacionResponseDTO crearNotificacion(NotificacionRequestDTO dto) {
        logger.info("Creando notificación tipo {} para destinatario {}",
                dto.getTipo(),
                dto.getDestinatario());

        Notificacion notificacion = new Notificacion();
        notificacion.setDestinatario(dto.getDestinatario());
        notificacion.setMensaje(dto.getMensaje());
        notificacion.setTipo(dto.getTipo());
        notificacion.setEstado("PENDIENTE");
        notificacion.setCanal(dto.getCanal() != null ? dto.getCanal() : "EMAIL");
        notificacion.setPrioridad(dto.getPrioridad() != null ? dto.getPrioridad() : "MEDIA");

        Notificacion guardada = notificacionRepository.save(notificacion);
        logger.info("Notificación registrada correctamente");

        return mapearAResponse(guardada);
    }

    public NotificacionResponseDTO actualizarNotificacion(Long id, NotificacionRequestDTO dto) {
        Notificacion notificacion = obtenerEntidadPorId(id);

        notificacion.setDestinatario(dto.getDestinatario());
        notificacion.setMensaje(dto.getMensaje());
        notificacion.setTipo(dto.getTipo());
        notificacion.setCanal(dto.getCanal() != null ? dto.getCanal() : "EMAIL");
        notificacion.setPrioridad(dto.getPrioridad() != null ? dto.getPrioridad() : "MEDIA");

        return mapearAResponse(notificacionRepository.save(notificacion));
    }

    public void marcarEnviada(Long id) {
        Notificacion notificacion = obtenerEntidadPorId(id);
        notificacion.setEstado("ENVIADA");
        notificacionRepository.save(notificacion);
    }

    public List<NotificacionResponseDTO> buscarPorEstado(String estado) {
        return notificacionRepository.findByEstadoIgnoreCase(estado)
                .stream()
                .map(this::mapearAResponse)
                .toList();
    }

    public List<NotificacionResponseDTO> buscarPorTipo(String tipo) {
        return notificacionRepository.findByTipoIgnoreCase(tipo)
                .stream()
                .map(this::mapearAResponse)
                .toList();
    }

    public List<NotificacionResponseDTO> buscarPorPrioridad(String prioridad) {
        return notificacionRepository.findByPrioridadIgnoreCase(prioridad)
                .stream()
                .map(this::mapearAResponse)
                .toList();
    }

    private Notificacion obtenerEntidadPorId(Long id) {
        return notificacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada con ID: " + id));
    }

    private NotificacionResponseDTO mapearAResponse(Notificacion notificacion) {
        return new NotificacionResponseDTO(
                notificacion.getIdNotificacion(),
                notificacion.getDestinatario(),
                notificacion.getMensaje(),
                notificacion.getTipo(),
                notificacion.getEstado(),
                notificacion.getCanal(),
                notificacion.getPrioridad()
        );
    }
}