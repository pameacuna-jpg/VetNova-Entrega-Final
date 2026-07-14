package com.vetnova.inventario.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class OrigenConsumoTest {

    @Test
    void values_debeContenerLosTresOrigenes() {
        OrigenConsumo[] valores = OrigenConsumo.values();

        assertEquals(3, valores.length);
        assertEquals(OrigenConsumo.VENTA, valores[0]);
        assertEquals(OrigenConsumo.ATENCION_CLINICA, valores[1]);
        assertEquals(OrigenConsumo.AJUSTE_ADMINISTRATIVO, valores[2]);
    }

    @Test
    void valueOf_debeRetornarElEnumCorrespondiente() {
        assertEquals(OrigenConsumo.VENTA, OrigenConsumo.valueOf("VENTA"));
        assertEquals(OrigenConsumo.ATENCION_CLINICA, OrigenConsumo.valueOf("ATENCION_CLINICA"));
        assertEquals(OrigenConsumo.AJUSTE_ADMINISTRATIVO, OrigenConsumo.valueOf("AJUSTE_ADMINISTRATIVO"));
    }

    @Test
    void name_debeRetornarElNombreCorrecto() {
        assertNotNull(OrigenConsumo.VENTA.name());
        assertEquals("VENTA", OrigenConsumo.VENTA.name());
    }
}
