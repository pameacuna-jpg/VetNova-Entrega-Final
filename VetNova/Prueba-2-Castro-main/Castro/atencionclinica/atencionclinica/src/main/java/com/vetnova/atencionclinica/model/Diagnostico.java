package com.vetnova.atencionclinica.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "diagnosticos")
public class Diagnostico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDiagnostico;

    // 1. Registrar diagnóstico
    @NotBlank(message = "La descripción del diagnóstico no puede estar vacía")
    private String descripcion; 

    // 2. Registrar tratamiento
    private String tratamiento; 

    // 3. Emitir receta
    private String recetaMedica; 

    // 4. Emitir certificado
    private String detalleCertificado; 

    private LocalDateTime fecha = LocalDateTime.now();

    @NotNull(message = "El ID del veterinario responsable es obligatorio")
    private Long idVeterinario; 

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ficha", nullable = false)
    @JsonBackReference 
    private FichaClinica fichaClinica;
}