package com.vetnova.ventas;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Disabled("Se desactiva contextLoads porque requiere contexto completo y no valida lógica de negocio")
@SpringBootTest
class VentasApplicationTests {

    @Test
    void contextLoads() {
    }
}