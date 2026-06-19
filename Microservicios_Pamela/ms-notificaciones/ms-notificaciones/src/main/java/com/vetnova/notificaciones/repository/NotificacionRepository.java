package com.vetnova.notificaciones.repository;

import com.vetnova.notificaciones.model.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;


public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    List<Notificacion> findByEstadoIgnoreCase(String estado);

    List<Notificacion> findByTipoIgnoreCase(String tipo);

    List<Notificacion> findByPrioridadIgnoreCase(String prioridad);
}