package com.vetnova.inventario.dto;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Evento de aplicación interconectado asíncronamente para notificar 
 * cuando un producto del inventario alcanza niveles de stock crítico.
 * Cumple con el estándar de comunicación desacoplada de VetNova.
 */
@Getter
public class StockBajoEvent extends ApplicationEvent {
    
    private final String nombreProducto;
    private final int stockActual;
    private final int stockMinimo;
    private final Long idSucursal;

    // El constructor debe pasar la referencia del objeto emisor (source) a la clase ApplicationEvent
    public StockBajoEvent(Object source, String nombreProducto, int stockActual, int stockMinimo, Long idSucursal) {
        super(source);
        this.nombreProducto = nombreProducto;
        this.stockActual = stockActual;
        this.stockMinimo = stockMinimo;
        this.idSucursal = idSucursal;
    }
}