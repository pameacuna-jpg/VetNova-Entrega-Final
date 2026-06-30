package com.vetnova.auth.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class SecurityConfigTest {

    @Test
    void passwordEncoder_debeCodificarYValidarPassword() {

        SecurityConfig securityConfig = new SecurityConfig();

        PasswordEncoder encoder = securityConfig.passwordEncoder();

        String passwordPlano = "123456";
        String passwordCodificado = encoder.encode(passwordPlano);

        assertNotNull(passwordCodificado);
        assertNotEquals(passwordPlano, passwordCodificado);
        assertTrue(encoder.matches(passwordPlano, passwordCodificado));
        assertFalse(encoder.matches("password-incorrecta", passwordCodificado));
    }
}