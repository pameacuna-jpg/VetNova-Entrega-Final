package com.vetnova.inventario.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductoResponseDTO {

    private Long idProducto;
    private String nombre;
    private String categoria;
    private Integer precio;
    private Integer stockActual;
    private Integer stockMinimo;
    private Boolean activo;
}