package com.vetnova.inventario.dto;

import com.vetnova.inventario.enums.OrigenConsumo;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConsumoInventarioRequestDTO {

    @NotNull(message = "El idProducto es obligatorio")
    private Long idProducto;

    @NotNull(message = "El idSucursal es obligatorio")
    private Long idSucursal;

    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad debe ser mayor a cero")
    private Integer cantidad;

    @NotNull(message = "El origen del consumo es obligatorio")
    private OrigenConsumo origen;

    private Long idReferencia;

    @Size(max = 255, message = "La observación no puede superar los 255 caracteres")
    private String observacion;
}