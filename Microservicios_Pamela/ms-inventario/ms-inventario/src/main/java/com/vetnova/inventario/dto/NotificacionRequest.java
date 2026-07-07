package com.vetnova.inventario.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionRequest {

    private String destinatario;
    private String mensaje;
    private String tipo;
    private String canal;
    private String prioridad;
}