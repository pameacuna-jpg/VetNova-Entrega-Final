package com.vetnova.inventario.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

class RestTemplateConfigTest {

    @Test
    void restTemplate_debeCrearUnaInstanciaValida() {
        RestTemplateConfig config = new RestTemplateConfig();
        RestTemplate restTemplate = config.restTemplate(new RestTemplateBuilder());
        assertNotNull(restTemplate);
    }
}