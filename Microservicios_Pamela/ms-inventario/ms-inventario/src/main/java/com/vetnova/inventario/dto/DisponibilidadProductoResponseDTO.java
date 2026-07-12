package com.vetnova.inventario.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DisponibilidadProductoResponseDTO {

    private Long idProducto;
    private String nombreProducto;
    private Integer stockActual;
    private Integer cantidadSolicitada;
    private boolean disponible;
    private boolean activo;
    private String mensaje;
}