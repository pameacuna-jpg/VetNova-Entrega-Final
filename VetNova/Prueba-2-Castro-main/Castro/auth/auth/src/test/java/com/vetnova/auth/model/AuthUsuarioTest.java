package com.vetnova.auth.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthUsuarioTest {

    @Test
    void prePersist_debeAsignarFechaYActivo() {

        AuthUsuario usuario = new AuthUsuario();
        usuario.setEmail("test@test.cl");
        usuario.setPassword("123");
        usuario.setRol("ADMIN");

        usuario.prePersist();

        assertNotNull(usuario.getFechaCreacion());
        assertTrue(usuario.getActivo());
    }

    @Test
    void prePersist_noDebeModificarActivoSiYaExiste() {

        AuthUsuario usuario = new AuthUsuario();
        usuario.setActivo(false);

        usuario.prePersist();

        assertFalse(usuario.getActivo());
        assertNotNull(usuario.getFechaCreacion());
    }

    @Test
    void builder_debeConstruirUsuario() {

        AuthUsuario usuario = AuthUsuario.builder()
                .id(1L)
                .email("correo@test.cl")
                .password("123")
                .rol("ADMIN")
                .activo(true)
                .build();

        assertEquals(1L, usuario.getId());
        assertEquals("correo@test.cl", usuario.getEmail());
        assertEquals("ADMIN", usuario.getRol());
        assertTrue(usuario.getActivo());
    }
}