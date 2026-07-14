package com.vetnova.atencionclinica.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

class ConsumoInventarioRequestTest {

    @Test
    void constructorVacio_debeCrearInstancia() {
        ConsumoInventarioRequest request = new ConsumoInventarioRequest();
        assertNotNull(request);
    }

    @Test
    void constructorConParametros_yGettersSetters_debenFuncionarCorrectamente() {
        ConsumoInventarioRequest request = new ConsumoInventarioRequest(
                1L, 2L, 3, "VENTA", 10L, "Observación"
        );

        assertEquals(1L, request.getIdProducto());
        assertEquals(2L, request.getIdSucursal());
        assertEquals(3, request.getCantidad());
        assertEquals("VENTA", request.getOrigen());
        assertEquals(10L, request.getIdReferencia());
        assertEquals("Observación", request.getObservacion());

        request.setIdProducto(5L);
        request.setIdSucursal(6L);
        request.setCantidad(7);
        request.setOrigen("DEVOLUCION");
        request.setIdReferencia(20L);
        request.setObservacion("Otra observación");

        assertEquals(5L, request.getIdProducto());
        assertEquals(6L, request.getIdSucursal());
        assertEquals(7, request.getCantidad());
        assertEquals("DEVOLUCION", request.getOrigen());
        assertEquals(20L, request.getIdReferencia());
        assertEquals("Otra observación", request.getObservacion());
    }
}