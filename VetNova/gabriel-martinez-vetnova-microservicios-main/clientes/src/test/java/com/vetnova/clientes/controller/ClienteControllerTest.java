package com.vetnova.clientes.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vetnova.clientes.dto.ClienteRequestDTO;
import com.vetnova.clientes.dto.ClienteResponseDTO;
import com.vetnova.clientes.service.IClienteService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClienteController.class)
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IClienteService clienteService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registrarCliente_debeRetornarCreated() throws Exception {
        ClienteRequestDTO request = crearRequestValido();
        ClienteResponseDTO response = crearResponse();

        Mockito.when(clienteService.registrar(any(ClienteRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idCliente").value(1L))
                .andExpect(jsonPath("$.rut").value("12345678-9"))
                .andExpect(jsonPath("$.nombre").value("Juan Perez"))
                .andExpect(jsonPath("$.email").value("juan@correo.cl"));
    }

    @Test
    void actualizarCliente_debeRetornarOk() throws Exception {
        ClienteRequestDTO request = crearRequestValido();
        ClienteResponseDTO response = crearResponse();
        response.setNombre("Juan Actualizado");

        Mockito.when(clienteService.actualizarDatos(eq(1L), any(ClienteRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/v1/clientes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCliente").value(1L))
                .andExpect(jsonPath("$.nombre").value("Juan Actualizado"));
    }

    @Test
    void buscarClientes_debeRetornarPagina() throws Exception {
        ClienteResponseDTO response = crearResponse();
        Page<ClienteResponseDTO> pagina = new PageImpl<>(List.of(response));

        Mockito.when(clienteService.buscarClientes(eq("Juan"), any()))
                .thenReturn(pagina);

        mockMvc.perform(get("/api/v1/clientes/buscar")
                        .param("termino", "Juan")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].idCliente").value(1L))
                .andExpect(jsonPath("$.content[0].nombre").value("Juan Perez"));
    }

    @Test
    void obtenerDetalleCompleto_debeRetornarCliente() throws Exception {
        ClienteResponseDTO response = crearResponse();

        Mockito.when(clienteService.obtenerDetalleCliente(1L))
                .thenReturn(response);

        mockMvc.perform(get("/api/v1/clientes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCliente").value(1L))
                .andExpect(jsonPath("$.rut").value("12345678-9"))
                .andExpect(jsonPath("$.nombre").value("Juan Perez"));
    }

    @Test
    void anonimizarClienteLegales_debeRetornarNoContent() throws Exception {
        Mockito.doNothing()
                .when(clienteService)
                .eliminar(1L);

        mockMvc.perform(delete("/api/v1/clientes/1/anonimizar"))
                .andExpect(status().isNoContent());

        Mockito.verify(clienteService).eliminar(1L);
    }

    @Test
    void registrarCliente_conDatosInvalidos_debeRetornarBadRequest() throws Exception {
        ClienteRequestDTO request = new ClienteRequestDTO();
        request.setRut("");
        request.setNombre("");
        request.setTelefono("abc");
        request.setEmail("correo-invalido");

        mockMvc.perform(post("/api/v1/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    private ClienteRequestDTO crearRequestValido() {
        return ClienteRequestDTO.builder()
                .rut("12345678-9")
                .nombre("Juan Perez")
                .telefono("+56912345678")
                .email("juan@correo.cl")
                .direccion("Av. Siempre Viva 123")
                .contactos(null)
                .build();
    }

    private ClienteResponseDTO crearResponse() {
        return ClienteResponseDTO.builder()
                .idCliente(1L)
                .rut("12345678-9")
                .nombre("Juan Perez")
                .telefono("+56912345678")
                .email("juan@correo.cl")
                .direccion("Av. Siempre Viva 123")
                .estado("ACTIVO")
                .fechaCreacion(LocalDateTime.now())
                .contactos(null)
                .historial(null)
                .build();
    }
}
