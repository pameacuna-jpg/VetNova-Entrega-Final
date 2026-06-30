package com.vetnova.auth.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {

        jwtUtil = new JwtUtil();

        ReflectionTestUtils.setField(
                jwtUtil,
                "secret",
                "12345678901234567890123456789012345678901234567890"
        );

        ReflectionTestUtils.setField(
                jwtUtil,
                "expiration",
                3600000L
        );

        jwtUtil.init();
    }

    @Test
    void generarToken_debeCrearTokenValido() {

        String token =
                jwtUtil.generarToken("pamela@test.cl", "ADMIN");

        assertNotNull(token);
        assertTrue(jwtUtil.validarToken(token));
    }

    @Test
    void obtenerEmail_debeRetornarEmail() {

        String token =
                jwtUtil.generarToken("pamela@test.cl", "ADMIN");

        String email =
                jwtUtil.obtenerEmail(token);

        assertEquals("pamela@test.cl", email);
    }

    @Test
    void validarToken_invalido_debeRetornarFalse() {

        boolean valido =
                jwtUtil.validarToken("token-invalido");

        assertFalse(valido);
    }

    @Test
    void obtenerClaims_debeRetornarRol() {

        String token =
                jwtUtil.generarToken("pamela@test.cl", "ADMIN");

        String rol =
                jwtUtil.obtenerClaims(token)
                        .get("rol", String.class);

        assertEquals("ADMIN", rol);
    }
}