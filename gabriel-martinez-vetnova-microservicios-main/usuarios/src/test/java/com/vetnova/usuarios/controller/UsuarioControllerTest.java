package com.vetnova.usuarios.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vetnova.usuarios.dto.UsuarioRequestDTO;
import com.vetnova.usuarios.dto.UsuarioResponseDTO;
import com.vetnova.usuarios.service.IUsuarioService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsuarioController.class)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IUsuarioService usuarioService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registrarUsuario_debeRetornarCreated() throws Exception {
        UsuarioRequestDTO request = crearRequest();
        UsuarioResponseDTO response = crearResponse();

        Mockito.when(usuarioService.registrarUsuario(any(UsuarioRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idUsuario").value(1))
                .andExpect(jsonPath("$.nombre").value("Administrador"))
                .andExpect(jsonPath("$.email").value("admin@vetnova.cl"));
    }

    @Test
    void listarUsuarios_debeRetornarOk() throws Exception {
        UsuarioResponseDTO response = crearResponse();

        // Simulamos que el servicio devuelve una lista con nuestro usuario de prueba
        Mockito.when(usuarioService.listarTodosLosUsuarios())
                .thenReturn(List.of(response));

        // Ejecutamos la petición GET mediante MockMvc
        mockMvc.perform(get("/api/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idUsuario").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Administrador"))
                .andExpect(jsonPath("$[0].email").value("admin@vetnova.cl"));
    }

    @Test
    void actualizarRoles_debeRetornarOk() throws Exception {
        UsuarioResponseDTO response = crearResponse();

        Mockito.when(usuarioService.actualizarRoles(eq(1L), anySet()))
                .thenReturn(response);

        mockMvc.perform(put("/api/v1/usuarios/1/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Set.of(1L, 2L))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idUsuario").value(1))
                .andExpect(jsonPath("$.nombre").value("Administrador"));
    }

    @Test
    void desactivarUsuario_debeRetornarNoContent() throws Exception {
        Mockito.doNothing()
                .when(usuarioService)
                .desactivarUsuarioLogico(1L);

        mockMvc.perform(delete("/api/v1/usuarios/1"))
                .andExpect(status().isNoContent());

        Mockito.verify(usuarioService)
                .desactivarUsuarioLogico(1L);
    }

    @Test
    void registrarUsuario_invalido_debeRetornarBadRequest() throws Exception {
        UsuarioRequestDTO request = new UsuarioRequestDTO();

        mockMvc.perform(post("/api/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    private UsuarioRequestDTO crearRequest() {
        return UsuarioRequestDTO.builder()
                .nombre("Administrador")
                .email("admin@vetnova.cl")
                .password("123456")
                .idSucursal(1L)
                .idsRoles(Set.of(1L))
                .build();
    }

    private UsuarioResponseDTO crearResponse() {
        return UsuarioResponseDTO.builder()
                .idUsuario(1L)
                .nombre("Administrador")
                .email("admin@vetnova.cl")
                .estado("ACTIVO")
                .idSucursal(1L)
                .roles(List.of("ADMIN"))
                .build();
    }
}