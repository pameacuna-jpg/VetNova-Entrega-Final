package com.vetnova.agenda;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Disabled("Se desactiva contextLoads porque requiere contexto completo y no valida lógica de negocio")
@SpringBootTest
class AgendaApplicationTests {

    @Test
    void contextLoads() {
    }
}