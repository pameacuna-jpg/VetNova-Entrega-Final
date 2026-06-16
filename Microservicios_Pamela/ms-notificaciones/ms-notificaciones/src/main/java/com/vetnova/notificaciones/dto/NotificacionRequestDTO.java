package com.vetnova.notificaciones.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NotificacionRequestDTO {

    @NotBlank(message = "El destinatario es obligatorio")
    private String destinatario;

    @NotBlank(message = "El mensaje es obligatorio")
    private String mensaje;

    @NotBlank(message = "El tipo es obligatorio")
    private String tipo;

    private String canal = "EMAIL";

    private String prioridad = "MEDIA";
}