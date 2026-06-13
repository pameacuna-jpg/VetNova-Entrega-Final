package com.vetnova.notificaciones.repository;

import com.vetnova.notificaciones.model.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    List<Notificacion> findByEstadoIgnoreCase(String estado);

    List<Notificacion> findByTipoIgnoreCase(String tipo);

    List<Notificacion> findByPrioridadIgnoreCase(String prioridad);
}