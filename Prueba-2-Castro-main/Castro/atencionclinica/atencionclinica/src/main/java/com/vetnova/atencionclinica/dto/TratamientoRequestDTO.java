package com.vetnova.atencionclinica.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TratamientoRequestDTO {

    @NotBlank(message = "El tratamiento es obligatorio")
    @Size(max = 1000, message = "El tratamiento no puede exceder 1000 caracteres")
    private String tratamiento;
}
