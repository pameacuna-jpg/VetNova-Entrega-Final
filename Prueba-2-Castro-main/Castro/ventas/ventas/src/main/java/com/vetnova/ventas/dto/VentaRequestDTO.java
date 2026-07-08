package com.vetnova.ventas.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VentaRequestDTO {
    @NotNull(message = "El ID del cliente es obligatorio")
    private Long idCliente;
    
    @NotNull(message = "El ID del producto es obligatorio")
    private Long idProducto;

    @NotNull(message = "El ID de la sucursal es obligatorio")
    private Long idSucursal;
    
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;
    
    @NotNull(message = "El monto total es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto total debe ser mayor a 0")
    private Double montoTotal;
}