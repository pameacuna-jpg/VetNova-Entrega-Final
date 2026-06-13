package com.vetnova.sucursales.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "boxes_atencion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoxAtencion {

    @Id
    @SequenceGenerator(
            name = "box_seq",
            sequenceName = "box_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "box_seq"
    )
    private Long idBox;

    @NotBlank(message = "El nombre del box es obligatorio")
    @Column(nullable = false)
    private String nombre;

    @NotBlank(message = "El tipo de atención es obligatorio")
    @Column(nullable = false)
    private String tipoAtencion;

    @NotNull(message = "El idSucursal es obligatorio")
    @Column(nullable = false)
    private Long idSucursal;

    @Column(nullable = false)
    private Boolean disponible = true;
}
