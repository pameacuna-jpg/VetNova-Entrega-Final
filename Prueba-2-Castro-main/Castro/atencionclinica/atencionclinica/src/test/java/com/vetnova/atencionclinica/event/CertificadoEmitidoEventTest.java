package com.vetnova.atencionclinica.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

class CertificadoEmitidoEventTest {

    @Test
    void constructor_debeInicializarTodosLosCamposYPayload() {
        CertificadoEmitidoEvent evento = new CertificadoEmitidoEvent(
                1L, 2L, 3L, 4L, "Reposo por 5 días"
        );

        assertNotNull(evento.getEventId());
        assertEquals("CertificadoEmitido", evento.getEventType());
        assertEquals("ms-atencion-clinica", evento.getSource());
        assertNotNull(evento.getOccurredAt());
        assertNotNull(evento.getPayload());

        assertEquals(1L, evento.getIdDiagnostico());
        assertEquals(2L, evento.getIdVeterinario());
        assertEquals(3L, evento.getIdMascota());
        assertEquals(4L, evento.getIdCliente());
        assertEquals("Reposo por 5 días", evento.getDetalleCertificado());
    }

    @Test
    void payload_getters_debenRetornarLosValoresCorrectos() {
        CertificadoEmitidoEvent.Payload payload = new CertificadoEmitidoEvent.Payload(
                10L, 20L, 30L, 40L, "Detalle certificado"
        );

        assertEquals(10L, payload.getIdDiagnostico());
        assertEquals(20L, payload.getIdVeterinario());
        assertEquals(30L, payload.getIdMascota());
        assertEquals(40L, payload.getIdCliente());
        assertEquals("Detalle certificado", payload.getDetalleCertificado());
    }
}