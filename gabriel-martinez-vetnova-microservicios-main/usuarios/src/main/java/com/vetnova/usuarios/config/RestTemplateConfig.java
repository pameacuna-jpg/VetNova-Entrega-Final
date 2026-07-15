package com.vetnova.usuarios.config;

import java.time.Duration;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    // Se usa RestTemplateBuilder en vez de "new RestTemplate()" para poder fijar
    // timeouts explícitos: si el microservicio remoto no responde, la petición
    // falla en 3s en vez de quedar colgada indefinidamente.
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .connectTimeout(Duration.ofSeconds(3))
                .readTimeout(Duration.ofSeconds(3))
                .build();
    }
}