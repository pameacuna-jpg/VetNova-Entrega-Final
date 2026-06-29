package com.vetnova.auth.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AuthUsuarioTest {

    @Test
    void testCreacionYAsignacionDeDatos() {
        // Given: Creamos la instancia del usuario
        AuthUsuario usuario = new AuthUsuario();
        LocalDateTime ahora = LocalDateTime.now();

        // When: Asignamos los valores usando los setters (generados por @Data)
        usuario.setId(1L);
        usuario.setEmail("correo@test.cl");
        usuario.setPassword("123");
        usuario.setRol("ADMIN");
        usuario.setActivo(true);
        usuario.setFechaCreacion(ahora);

        // Then: Verificamos que los getters retornen la información correcta
        assertEquals(1L, usuario.getId());
        assertEquals("correo@test.cl", usuario.getEmail());
        assertEquals("123", usuario.getPassword());
        assertEquals("ADMIN", usuario.getRol());
        assertTrue(usuario.getActivo());
        assertNotNull(usuario.getFechaCreacion());
        assertEquals(ahora, usuario.getFechaCreacion());
    }

    @Test
    void testUsuarioInactivo() {
        // Given & When: Probamos el comportamiento del estado activo/inactivo
        AuthUsuario usuario = new AuthUsuario();
        usuario.setActivo(false);

        // Then
        assertFalse(usuario.getActivo());
    }
}