package com.vetnova.agenda.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "citas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El ID del cliente es obligatorio")
    private Long idCliente;

    @NotNull(message = "El ID de la mascota es obligatorio")
    private Long idMascota;

    @NotNull(message = "El ID del veterinario es obligatorio")
    private Long idVeterinario;

    @NotNull(message = "La fecha y hora de la cita son obligatorias")
    @FutureOrPresent(message = "La fecha de la cita no puede ser en el pasado")
    private LocalDateTime fechaHora;

    @NotBlank(message = "El motivo de la consulta es obligatorio")
    @Size(min = 5, max = 200, message = "El motivo debe tener entre 5 y 200 caracteres")
    private String motivo;

    // Estados: AGENDADA, CONFIRMADA, REPROGRAMADA, CANCELADA
    private String estado;
}