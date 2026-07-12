package com.vetnova.inventario.dto;

import com.vetnova.inventario.enums.OrigenConsumo;
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
public class ConsumoInventarioResponseDTO {

    private Long idMovimiento;
    private Long idProducto;
    private Long idSucursal;
    private Integer cantidadConsumida;
    private Integer stockAnterior;
    private Integer stockActual;
    private OrigenConsumo origen;
    private Long idReferencia;
    private String mensaje;
}