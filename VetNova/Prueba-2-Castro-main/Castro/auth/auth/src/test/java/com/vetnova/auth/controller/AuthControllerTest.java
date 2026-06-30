package com.vetnova.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vetnova.auth.dto.AuthResponse;
import com.vetnova.auth.dto.LoginRequest;
import com.vetnova.auth.dto.RegistroUsuarioRequest;
import com.vetnova.auth.service.AuthService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registrar_debeRetornarAuthResponse() throws Exception {
        RegistroUsuarioRequest request = new RegistroUsuarioRequest();
        request.setEmail("admin@vetnova.cl");
        request.setPassword("123456");
        request.setRol("ADMIN");

        AuthResponse response = new AuthResponse(
                "token-test",
                "admin@vetnova.cl",
                "ADMIN"
        );

        Mockito.when(authService.registrar(any(RegistroUsuarioRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token-test"))
                .andExpect(jsonPath("$.email").value("admin@vetnova.cl"))
                .andExpect(jsonPath("$.rol").value("ADMIN"));
    }

    @Test
    void login_debeRetornarAuthResponse() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("admin@vetnova.cl");
        request.setPassword("123456");

        AuthResponse response = new AuthResponse(
                "token-test",
                "admin@vetnova.cl",
                "ADMIN"
        );

        Mockito.when(authService.login(any(LoginRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token-test"))
                .andExpect(jsonPath("$.email").value("admin@vetnova.cl"))
                .andExpect(jsonPath("$.rol").value("ADMIN"));
    }

    @Test
    void registrar_conDatosInvalidos_debeRetornarBadRequest() throws Exception {
        RegistroUsuarioRequest request = new RegistroUsuarioRequest();
        request.setEmail("correo-invalido");
        request.setPassword("");
        request.setRol("");

        mockMvc.perform(post("/api/v1/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_conDatosInvalidos_debeRetornarBadRequest() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("correo-invalido");
        request.setPassword("");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}