package com.vetnova.agenda.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CitaResponseDTO {

    private Long id;
    private Long idCliente;
    private Long idMascota;
    private Long idVeterinario;
    private LocalDateTime fechaHora;
    private String motivo;
    private String estado;
}