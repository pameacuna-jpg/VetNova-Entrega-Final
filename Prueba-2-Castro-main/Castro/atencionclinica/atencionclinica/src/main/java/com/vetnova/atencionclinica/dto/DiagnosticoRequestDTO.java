package com.vetnova.atencionclinica.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DiagnosticoRequestDTO {

    @NotBlank(message = "La descripción del diagnóstico es obligatoria")
    @Size(max = 1000, message = "La descripción no puede exceder 1000 caracteres")
    private String descripcion;

    @Size(max = 1000, message = "El tratamiento no puede exceder 1000 caracteres")
    private String tratamiento;

    @Size(max = 1000, message = "La receta médica no puede exceder 1000 caracteres")
    private String recetaMedica;

    @Size(max = 1000, message = "El detalle del certificado no puede exceder 1000 caracteres")
    private String detalleCertificado;

    @NotNull(message = "El ID del veterinario es obligatorio")
    private Long idVeterinario;

    @NotNull(message = "El ID de la ficha clínica es obligatorio")
    private Long idFicha;
}