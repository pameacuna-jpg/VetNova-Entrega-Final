package com.vetnova.agenda.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CitaRequestDTO {

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
}