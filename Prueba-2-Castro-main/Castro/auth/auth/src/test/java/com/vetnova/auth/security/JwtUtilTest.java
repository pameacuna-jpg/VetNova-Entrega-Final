package com.vetnova.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.Key;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    // Usamos una clave secreta larga de al menos 256 bits para pasar la seguridad de HS256
    private final String SECRET_KEY = "vetnova_auth_secret_key_2026_segura_para_desarrollo_vetnova";

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();

        // Inyectamos las propiedades simulando el @Value de Spring Boot
        ReflectionTestUtils.setField(jwtUtil, "secret", SECRET_KEY);
        ReflectionTestUtils.setField(jwtUtil, "expiration", 3600000L); // 1 hora
    }

    @Test
    void testGenerateToken_debeCrearTokenValidoConEmail() {
        // Given: Un correo de prueba
        String email = "pamela@test.cl";

        // When: Generamos el token usando nuestro método real de 1 parámetro
        String token = jwtUtil.generateToken(email);

        // Then: Verificamos que el token se haya creado y no esté vacío
        assertNotNull(token);
        assertFalse(token.trim().isEmpty());

        // Verificación de integridad: Desencriptamos el token generado para validar los Claims
        Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        // Validamos que el "Subject" (Sujeto) del token sea exactamente el correo que le pasamos
        assertEquals("pamela@test.cl", claims.getSubject());
        
        // Validamos que tenga fecha de emisión y fecha de expiración
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
    }
}