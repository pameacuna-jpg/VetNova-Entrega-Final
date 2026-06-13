package com.vetnova.notificaciones.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vetnova.notificaciones.model.Notificacion;
import com.vetnova.notificaciones.repository.NotificacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificacionService {

    private static final Logger logger =
            LoggerFactory.getLogger(NotificacionService.class);

    @Autowired
    private NotificacionRepository notificacionRepository;

    public List<Notificacion> listarNotificaciones() {

        return notificacionRepository.findAll();
    }

    public Notificacion buscarPorId(Long id) {

        return notificacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada con ID: " + id));
    }

    public Notificacion crearNotificacion(Notificacion notificacion) {

       logger.info("Creando notificación tipo {} para destinatario {}",
                notificacion.getTipo(),
                notificacion.getDestinatario()); 

        logger.info("Notificación registrada correctamente");        

        notificacion.setEstado("PENDIENTE");

        return notificacionRepository.save(notificacion);
    }

    public Notificacion actualizarNotificacion(Long id,
                                               Notificacion datos) {

        Notificacion notificacion = buscarPorId(id);

        notificacion.setDestinatario(datos.getDestinatario());
        notificacion.setMensaje(datos.getMensaje());
        notificacion.setTipo(datos.getTipo());
        notificacion.setCanal(datos.getCanal());
        notificacion.setPrioridad(datos.getPrioridad());

        return notificacionRepository.save(notificacion);
    }

    public void marcarEnviada(Long id) {

        Notificacion notificacion = buscarPorId(id);

        notificacion.setEstado("ENVIADA");

        notificacionRepository.save(notificacion);
    }

    public List<Notificacion> buscarPorEstado(String estado) {

        return notificacionRepository.findByEstadoIgnoreCase(estado);
    }

    public List<Notificacion> buscarPorTipo(String tipo) {

        return notificacionRepository.findByTipoIgnoreCase(tipo);
    }

    public List<Notificacion> buscarPorPrioridad(String prioridad) {

        return notificacionRepository.findByPrioridadIgnoreCase(prioridad);
    }
}