package com.vetnova.notificaciones.dto;

import com.vetnova.notificaciones.enums.CanalNotificacion;
import com.vetnova.notificaciones.enums.DestinoNotificacion;
import com.vetnova.notificaciones.enums.PrioridadNotificacion;
import com.vetnova.notificaciones.enums.TipoNotificacion;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificacionRequestDTO {

    @NotNull(message = "El destino de la notificación es obligatorio")
    private DestinoNotificacion destino;

    private Long idCliente;

    private String destinatario;

    @NotBlank(message = "El mensaje es obligatorio")
    @Size(min = 10, max = 500, message = "El mensaje debe tener entre 10 y 500 caracteres")
    private String mensaje;

    @NotNull(message = "El tipo de notificación es obligatorio")
    private TipoNotificacion tipo;

    @NotNull(message = "El canal de notificación es obligatorio")
    private CanalNotificacion canal;

    @NotNull(message = "La prioridad es obligatoria")
    private PrioridadNotificacion prioridad;
}