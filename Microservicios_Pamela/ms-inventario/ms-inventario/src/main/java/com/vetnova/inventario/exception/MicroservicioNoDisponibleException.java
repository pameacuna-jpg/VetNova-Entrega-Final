package com.vetnova.inventario.exception;

public class MicroservicioNoDisponibleException extends RuntimeException {

    public MicroservicioNoDisponibleException(String message) {
        super(message);
    }
}