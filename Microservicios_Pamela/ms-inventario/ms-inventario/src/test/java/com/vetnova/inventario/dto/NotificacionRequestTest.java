package com.vetnova.inventario.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class NotificacionRequestTest {

    @Test
    void constructorVacio_debeCrearInstancia() {
        NotificacionRequest request = new NotificacionRequest();
        assertNotNull(request);
    }

    @Test
    void constructorConParametros_yGettersSetters_debenFuncionarCorrectamente() {
        NotificacionRequest request = new NotificacionRequest(
                "SUCURSAL", 55L, "encargado@vetnova.cl", "Stock bajo", "INVENTARIO", "EMAIL", "ALTA"
        );

        assertEquals("SUCURSAL", request.getDestino());
        assertEquals(55L, request.getIdCliente());
        assertEquals("encargado@vetnova.cl", request.getDestinatario());
        assertEquals("Stock bajo", request.getMensaje());
        assertEquals("INVENTARIO", request.getTipo());
        assertEquals("EMAIL", request.getCanal());
        assertEquals("ALTA", request.getPrioridad());

        request.setDestino("CLIENTE");
        request.setIdCliente(99L);
        request.setDestinatario("cliente@vetnova.cl");
        request.setMensaje("Otro mensaje");
        request.setTipo("VENTA");
        request.setCanal("SMS");
        request.setPrioridad("MEDIA");

        assertEquals("CLIENTE", request.getDestino());
        assertEquals(99L, request.getIdCliente());
        assertEquals("cliente@vetnova.cl", request.getDestinatario());
        assertEquals("Otro mensaje", request.getMensaje());
        assertEquals("VENTA", request.getTipo());
        assertEquals("SMS", request.getCanal());
        assertEquals("MEDIA", request.getPrioridad());
    }
}
