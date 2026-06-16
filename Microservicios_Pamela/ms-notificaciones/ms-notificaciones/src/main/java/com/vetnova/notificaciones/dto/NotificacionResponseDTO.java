package com.vetnova.notificaciones.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificacionResponseDTO {

    private Long idNotificacion;
    private String destinatario;
    private String mensaje;
    private String tipo;
    private String estado;
    private String canal;
    private String prioridad;
}
