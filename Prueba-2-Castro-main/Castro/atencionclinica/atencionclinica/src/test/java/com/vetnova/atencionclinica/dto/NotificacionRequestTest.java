package com.vetnova.atencionclinica.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

class NotificacionRequestTest {

    @Test
    void constructorVacio_debeCrearInstancia() {
        NotificacionRequest request = new NotificacionRequest();
        assertNotNull(request);
    }

    @Test
    void constructorConParametros_yGettersSetters_debenFuncionarCorrectamente() {
        NotificacionRequest request = new NotificacionRequest(
                "CLIENTE", 55L, "Mensaje de prueba", "VENTA", "EMAIL", "MEDIA"
        );

        assertEquals("CLIENTE", request.getDestino());
        assertEquals(55L, request.getIdCliente());
        assertEquals("Mensaje de prueba", request.getMensaje());
        assertEquals("VENTA", request.getTipo());
        assertEquals("EMAIL", request.getCanal());
        assertEquals("MEDIA", request.getPrioridad());

        request.setDestino("VETERINARIO");
        request.setIdCliente(99L);
        request.setMensaje("Otro mensaje");
        request.setTipo("ATENCION");
        request.setCanal("SMS");
        request.setPrioridad("ALTA");

        assertEquals("VETERINARIO", request.getDestino());
        assertEquals(99L, request.getIdCliente());
        assertEquals("Otro mensaje", request.getMensaje());
        assertEquals("ATENCION", request.getTipo());
        assertEquals("SMS", request.getCanal());
        assertEquals("ALTA", request.getPrioridad());
    }
}