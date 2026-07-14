package com.vetnova.ventas.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

class MovimientoInventarioRequestTest {

    @Test
    void constructorVacio_debeCrearInstancia() {
        MovimientoInventarioRequest request = new MovimientoInventarioRequest();
        assertNotNull(request);
    }

    @Test
    void constructorConParametros_yGettersSetters_debenFuncionarCorrectamente() {
        MovimientoInventarioRequest request = new MovimientoInventarioRequest(
                1L, 2L, "ENTRADA", 3, "Entrada automática por devolución"
        );

        assertEquals(1L, request.getIdProducto());
        assertEquals(2L, request.getIdSucursal());
        assertEquals("ENTRADA", request.getTipoMovimiento());
        assertEquals(3, request.getCantidad());
        assertEquals("Entrada automática por devolución", request.getObservacion());

        request.setIdProducto(5L);
        request.setIdSucursal(6L);
        request.setTipoMovimiento("SALIDA");
        request.setCantidad(7);
        request.setObservacion("Otra observación");

        assertEquals(5L, request.getIdProducto());
        assertEquals(6L, request.getIdSucursal());
        assertEquals("SALIDA", request.getTipoMovimiento());
        assertEquals(7, request.getCantidad());
        assertEquals("Otra observación", request.getObservacion());
    }
}