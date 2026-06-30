package com.vetnova.auth.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Genera getters, setters, toString automáticamente [3]
@NoArgsConstructor // Constructor vacío exigido por JPA [5]
@AllArgsConstructor // Constructor con todos los parámetros [3]
@Entity // Le dice a Spring que esta clase es una tabla en BD [4]
@Table(name = "auth_usuarios")
public class AuthUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Autoincrementable [6]
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String rol; // Ej: "ADMIN", "VETERINARIO", "CLIENTE"
}