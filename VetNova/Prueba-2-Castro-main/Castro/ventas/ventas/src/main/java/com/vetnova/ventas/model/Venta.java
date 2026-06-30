package com.vetnova.ventas.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "ventas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long idCliente;
    private Long idProducto;
    private Integer cantidad;
    private Double montoTotal;
    private String estado; // Posibles estados: PENDIENTE, PAGADA, DEVUELTA
    private LocalDateTime fechaVenta;
}