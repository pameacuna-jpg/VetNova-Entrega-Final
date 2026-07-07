package com.vetnova.sucursales.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValidacionSucursalResponseDTO {

    private Long idSucursal;
    private boolean existe;
    private boolean activa;
    private String mensaje;
}