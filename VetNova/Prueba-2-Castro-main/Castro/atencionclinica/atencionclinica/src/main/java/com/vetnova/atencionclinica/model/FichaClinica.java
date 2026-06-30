package com.vetnova.atencionclinica.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.Valid; // <-- IMPORTANTE: Faltaba esta importación
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "fichas_clinicas")
public class FichaClinica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idFicha;

    @NotNull(message = "El ID de la mascota es obligatorio")
    @Column(nullable = false)
    private Long idMascota; // Conexión lógica con el MS de Mascotas

    private LocalDateTime fechaCreacion = LocalDateTime.now();

    private String observaciones;

    // Relación exigida por rúbrica: Una ficha tiene muchos diagnósticos
    @Valid // <-- IMPORTANTE: Esta es la anotación que activa la validación en cascada
    @OneToMany(mappedBy = "fichaClinica", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference // Evita bucles infinitos al devolver JSON
    private List<Diagnostico> diagnosticos = new ArrayList<>();
}