package com.vetnova.auth.event;

import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class EventoDominioTest {

    @Test
    void testCrearEventoDominio() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("email", "test@vetnova.cl");

        EventoDominio<Map<String, Object>> evento = new EventoDominio<>(
                "LoginExitoso",
                "ms-auth",
                payload
        );

        assertEquals("LoginExitoso", evento.getEventType());
        assertEquals("ms-auth", evento.getSource());
        assertEquals(payload, evento.getPayload());
        assertNotNull(evento.getTimestamp());
    }
}