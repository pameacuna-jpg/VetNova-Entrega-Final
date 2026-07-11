package com.vetnova.notificaciones.dto;

import com.vetnova.notificaciones.enums.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificacionResponseDTO {

    private Long idNotificacion;
    private DestinoNotificacion destino;
    private Long idCliente;
    private String destinatario;
    private String mensaje;
    private TipoNotificacion tipo;
    private EstadoNotificacion estado;
    private CanalNotificacion canal;
    private PrioridadNotificacion prioridad;
    private LocalDateTime fechaCreacion;
}