package com.vetnova.sucursales.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "horarios_sucursal")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HorarioSucursal {

    @Id
    @SequenceGenerator(
            name = "horario_seq",
            sequenceName = "horario_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "horario_seq"
    )
    private Long idHorario;

    @NotBlank(message = "El día es obligatorio")
    @Column(nullable = false)
    private String dia;

    @NotBlank(message = "La hora de apertura es obligatoria")
    @Column(nullable = false)
    private String horaApertura;

    @NotBlank(message = "La hora de cierre es obligatoria")
    @Column(nullable = false)
    private String horaCierre;

    @NotNull(message = "El idSucursal es obligatorio")
    @Column(nullable = false)
    private Long idSucursal;

    @Column(nullable = false)
    private Boolean activo = true;
}
