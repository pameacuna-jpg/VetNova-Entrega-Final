package com.vetnova.notificaciones.repository;

import com.vetnova.notificaciones.enums.EstadoNotificacion;
import com.vetnova.notificaciones.enums.TipoNotificacion;
import com.vetnova.notificaciones.model.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    List<Notificacion> findByIdCliente(Long idCliente);

    List<Notificacion> findByEstado(EstadoNotificacion estado);

    List<Notificacion> findByTipo(TipoNotificacion tipo);
}
