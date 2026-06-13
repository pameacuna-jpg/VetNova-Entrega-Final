package com.vetnova.notificaciones.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "notificaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notificacion {

    @Id
    @SequenceGenerator(
            name = "notificacion_seq",
            sequenceName = "notificacion_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "notificacion_seq"
    )
    private Long idNotificacion;

    @NotBlank(message = "El destinatario es obligatorio")
    @Column(nullable = false)
    private String destinatario;

    @NotBlank(message = "El mensaje es obligatorio")
    @Column(nullable = false, length = 500)
    private String mensaje;

    @NotBlank(message = "El tipo es obligatorio")
    @Column(nullable = false)
    private String tipo;

    @Column(nullable = false)
    private String estado = "PENDIENTE";

    @Column(nullable = false)
    private String canal = "EMAIL";

    @Column(nullable = false)
    private String prioridad = "MEDIA";
}
