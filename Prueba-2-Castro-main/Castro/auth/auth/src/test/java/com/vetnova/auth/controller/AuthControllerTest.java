package com.vetnova.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vetnova.auth.dto.LoginRequest;
import com.vetnova.auth.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // Desactiva los filtros de seguridad solo para poder probar el controlador aislado
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // Usamos MockitoBean (el nuevo estándar de Spring Boot 3.4) para simular la capa de servicio
    @MockitoBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        // Preparamos datos simulados de entrada
        loginRequest = new LoginRequest();
        loginRequest.setEmail("admin@vetnova.cl");
        loginRequest.setPassword("123456");
    }

    @Test
    void login_DeberiaRetornar200_Y_Token_CuandoDatosSonCorrectos() throws Exception {
        // Given: Le decimos al mock qué devolver cuando se ejecute el servicio
        when(authService.procesarLogin(any(LoginRequest.class))).thenReturn("eyJhbGciOiJIUzI1Ni...");

        // When & Then: Simulamos la petición POST de Postman
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Autenticación exitosa"))
                .andExpect(jsonPath("$.token").value("eyJhbGciOiJIUzI1Ni..."));
    }

    @Test
    void login_DeberiaRetornar400_SiDatosSonInvalidos() throws Exception {
        // Given: Un Request a medias para forzar que el @Valid del controlador explote (Fail-Fast)
        LoginRequest requestInvalido = new LoginRequest();
        requestInvalido.setPassword("123456"); // Olvidamos el email a propósito

        // When & Then: Verificamos que lance un Error 400 Bad Request
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void recuperarPassword_DeberiaRetornar200_Y_Mensaje() throws Exception {
        // Given
        Map<String, String> request = new HashMap<>();
        request.put("email", "admin@vetnova.cl");

        when(authService.recuperarContrasena("admin@vetnova.cl"))
                .thenReturn("Se han enviado las instrucciones a admin@vetnova.cl");

        // When & Then
        mockMvc.perform(post("/api/v1/auth/recuperar-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.mensaje").value("Se han enviado las instrucciones a admin@vetnova.cl"));
    }
}