package com.vetnova.inventario.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "productos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Producto {

    @Id
    @SequenceGenerator(
            name = "producto_seq",
            sequenceName = "producto_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "producto_seq"
    )
    private Long idProducto;

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(min = 2, max = 80, message = "El nombre debe tener entre 2 y 80 caracteres")
    @Column(nullable = false)
    private String nombre;

    @NotBlank(message = "La categoría es obligatoria")
    @Column(nullable = false)
    private String categoria;

    @NotNull(message = "El precio es obligatorio")
    @PositiveOrZero(message = "El precio no puede ser negativo")
    @Column(nullable = false)
    private Integer precio;

    @NotNull(message = "El stock actual es obligatorio")
    @PositiveOrZero(message = "El stock actual no puede ser negativo")
    @Column(nullable = false)
    private Integer stockActual;

    @NotNull(message = "El stock mínimo es obligatorio")
    @PositiveOrZero(message = "El stock mínimo no puede ser negativo")
    @Column(nullable = false)
    private Integer stockMinimo;

    @Column(nullable = false)
    private Boolean activo = true;
}
