package com.vetnova.inventario.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "proveedores")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Proveedor {

    @Id
    @SequenceGenerator(
            name = "proveedor_seq",
            sequenceName = "proveedor_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "proveedor_seq"
    )
    private Long idProveedor;

    @NotBlank(message = "El nombre del proveedor es obligatorio")
    @Column(nullable = false)
    private String nombre;

    @NotBlank(message = "El teléfono del proveedor es obligatorio")
    @Column(nullable = false)
    private String telefono;

    @Email(message = "El correo debe tener un formato válido")
    @NotBlank(message = "El correo del proveedor es obligatorio")
    @Column(nullable = false)
    private String email;

    @NotBlank(message = "La dirección del proveedor es obligatoria")
    @Column(nullable = false)
    private String direccion;

    @Column(nullable = false)
    private Boolean activo = true;
}