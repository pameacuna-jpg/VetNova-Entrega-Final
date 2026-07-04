package com.vetnova.atencionclinica.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CertificadoRequestDTO {

    @NotBlank(message = "El detalle del certificado es obligatorio")
    @Size(max = 1000, message = "El detalle del certificado no puede exceder 1000 caracteres")
    private String detalleCertificado;
}
