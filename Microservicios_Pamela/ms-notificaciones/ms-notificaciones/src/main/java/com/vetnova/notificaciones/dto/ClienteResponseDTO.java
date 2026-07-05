package com.vetnova.notificaciones.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteResponseDTO {

    private Long idCliente;
    private String rut;
    private String nombre;
    private String telefono;
    private String email;
    private String direccion;
    private String estado;
    private LocalDateTime fechaCreacion;
}