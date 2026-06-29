package com.vetnova.auth.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InvalidCredentialsExceptionTest {

    @Test
    void testCrearExcepcion_DebeGuardarElMensajeCorrectamente() {
        // Given: Un mensaje de error simulado
        String mensajeEsperado = "El correo o la contraseña son incorrectos";

        // When: Instanciamos nuestra excepción personalizada
        InvalidCredentialsException excepcion = new InvalidCredentialsException(mensajeEsperado);

        // Then: Verificamos que se haya creado y que el mensaje coincida exactamente
        assertNotNull(excepcion);
        assertEquals(mensajeEsperado, excepcion.getMessage());
    }
}