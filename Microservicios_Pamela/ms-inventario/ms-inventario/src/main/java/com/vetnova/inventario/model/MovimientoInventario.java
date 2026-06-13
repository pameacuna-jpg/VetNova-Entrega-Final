package com.vetnova.inventario.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "movimientos_inventario")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoInventario {

    @Id
    @SequenceGenerator(
            name = "movimiento_seq",
            sequenceName = "movimiento_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "movimiento_seq"
    )
    private Long idMovimiento;

    @NotNull(message = "El idProducto es obligatorio")
    @Column(nullable = false)
    private Long idProducto;

    @NotNull(message = "El idSucursal es obligatorio")
    @Column(nullable = false)
    private Long idSucursal;

    @NotBlank(message = "El tipo de movimiento es obligatorio")
    @Column(nullable = false)
    private String tipoMovimiento; // ENTRADA, SALIDA, AJUSTE

    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad debe ser mayor a cero")
    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false)
    private String observacion = "Sin observación";
}
