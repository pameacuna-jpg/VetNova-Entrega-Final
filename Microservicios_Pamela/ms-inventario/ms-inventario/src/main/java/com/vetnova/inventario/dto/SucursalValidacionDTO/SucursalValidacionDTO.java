package com.vetnova.inventario.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SucursalValidacionDTO {

    private Long idSucursal;
    private boolean existe;
    private boolean activa;
    private String mensaje;
}