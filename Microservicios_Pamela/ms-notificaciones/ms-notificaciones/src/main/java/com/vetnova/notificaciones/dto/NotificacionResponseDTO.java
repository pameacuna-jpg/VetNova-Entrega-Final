package com.vetnova.notificaciones.dto;

import com.vetnova.notificaciones.enums.CanalNotificacion;
import com.vetnova.notificaciones.enums.EstadoNotificacion;
import com.vetnova.notificaciones.enums.PrioridadNotificacion;
import com.vetnova.notificaciones.enums.TipoNotificacion;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificacionResponseDTO {

    private Long idNotificacion;
    private Long idCliente;
    private String destinatario;
    private String mensaje;
    private TipoNotificacion tipo;
    private EstadoNotificacion estado;
    private CanalNotificacion canal;
    private PrioridadNotificacion prioridad;
    private LocalDateTime fechaCreacion;
}