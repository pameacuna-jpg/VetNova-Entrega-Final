package com.vetnova.atencionclinica.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

class RecetaEmitidaEventTest {

    @Test
    void constructor_debeInicializarTodosLosCamposYPayload() {
        RecetaEmitidaEvent evento = new RecetaEmitidaEvent(
                1L, 2L, 3L, 4L, "Amoxicilina 500mg"
        );

        assertNotNull(evento.getEventId());
        assertEquals("RecetaEmitida", evento.getEventType());
        assertEquals("ms-atencion-clinica", evento.getSource());
        assertNotNull(evento.getOccurredAt());
        assertNotNull(evento.getPayload());

        assertEquals(1L, evento.getIdDiagnostico());
        assertEquals(2L, evento.getIdVeterinario());
        assertEquals(3L, evento.getIdMascota());
        assertEquals(4L, evento.getIdCliente());
        assertEquals("Amoxicilina 500mg", evento.getRecetaMedica());
    }

    @Test
    void payload_getters_debenRetornarLosValoresCorrectos() {
        RecetaEmitidaEvent.Payload payload = new RecetaEmitidaEvent.Payload(
                10L, 20L, 30L, 40L, "Receta médica"
        );

        assertEquals(10L, payload.getIdDiagnostico());
        assertEquals(20L, payload.getIdVeterinario());
        assertEquals(30L, payload.getIdMascota());
        assertEquals(40L, payload.getIdCliente());
        assertEquals("Receta médica", payload.getRecetaMedica());
    }
}