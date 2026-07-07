package com.vetnova.inventario.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SucursalValidacionDTO {

    private Long idSucursal;
    private String nombre;
    private String direccion;
    private Boolean activa;
}