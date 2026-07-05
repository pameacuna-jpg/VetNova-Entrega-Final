package com.vetnova.notificaciones.model;

import com.vetnova.notificaciones.enums.CanalNotificacion;
import com.vetnova.notificaciones.enums.EstadoNotificacion;
import com.vetnova.notificaciones.enums.PrioridadNotificacion;
import com.vetnova.notificaciones.enums.TipoNotificacion;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notificaciones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notificacion")
    private Long idNotificacion;

    @Column(name = "id_cliente", nullable = false)
    private Long idCliente;

    @Column(nullable = false, length = 120)
    private String destinatario;

    @Column(nullable = false, length = 500)
    private String mensaje;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoNotificacion tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoNotificacion estado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CanalNotificacion canal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PrioridadNotificacion prioridad;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @PrePersist
    public void prePersist() {
        if (estado == null) {
            estado = EstadoNotificacion.PENDIENTE;
        }

        if (canal == null) {
            canal = CanalNotificacion.EMAIL;
        }

        if (prioridad == null) {
            prioridad = PrioridadNotificacion.MEDIA;
        }

        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
    }
}