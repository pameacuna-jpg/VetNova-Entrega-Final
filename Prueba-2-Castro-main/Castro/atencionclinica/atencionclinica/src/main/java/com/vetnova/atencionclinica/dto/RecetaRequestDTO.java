package com.vetnova.atencionclinica.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RecetaRequestDTO {

    @NotBlank(message = "La receta médica es obligatoria")
    @Size(max = 1000, message = "La receta médica no puede exceder 1000 caracteres")
    private String recetaMedica;

    private Long idProducto; // Opcional: insumo/producto físico asociado a la receta

    private Integer cantidad; // Opcional: obligatorio solo si viene idProducto
}
