package com.vetnova.clientes.dto;

import lombok.Data;

@Data
public class NotificacionRequestDTO {

    private String destinatario;
    private String mensaje;
    private String tipo;
    private String canal = "EMAIL";
    private String prioridad = "MEDIA";
}
