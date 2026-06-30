package com.vetnova.mascotas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vetnova.mascotas.dto.MascotaRequestDTO;
import com.vetnova.mascotas.dto.MascotaResponseDTO;
import com.vetnova.mascotas.dto.TransferenciaRequestDTO;
import com.vetnova.mascotas.service.IMascotaService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MascotaController.class)
class MascotaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IMascotaService mascotaService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registrarMascota_debeRetornarCreated() throws Exception {

        MascotaRequestDTO request = crearRequest();
        MascotaResponseDTO response = crearResponse();

        Mockito.when(mascotaService.registrar(any(MascotaRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/mascotas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idMascota").value(1))
                .andExpect(jsonPath("$.nombre").value("Firulais"));
    }

    @Test
    void actualizarMascota_debeRetornarOk() throws Exception {

        MascotaRequestDTO request = crearRequest();
        MascotaResponseDTO response = crearResponse();

        Mockito.when(mascotaService.actualizar(eq(1L), any(MascotaRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/v1/mascotas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Firulais"));
    }

    @Test
    void obtenerFichaClinica_debeRetornarMascota() throws Exception {

        Mockito.when(mascotaService.obtenerPorId(1L))
                .thenReturn(crearResponse());

        mockMvc.perform(get("/api/v1/mascotas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idMascota").value(1))
                .andExpect(jsonPath("$.nombre").value("Firulais"));
    }

    @Test
    void buscarPacientes_debeRetornarPagina() throws Exception {

        Page<MascotaResponseDTO> pagina =
                new PageImpl<>(List.of(crearResponse()));

        Mockito.when(mascotaService.buscarPacientes(eq("Firulais"), any()))
                .thenReturn(pagina);

        mockMvc.perform(get("/api/v1/mascotas/buscar")
                        .param("termino", "Firulais"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nombre").value("Firulais"));
    }

    @Test
    void transferirPropietario_debeRetornarNoContent() throws Exception {

        TransferenciaRequestDTO dto =
                TransferenciaRequestDTO.builder()
                        .idNuevoCliente(2L)
                        .build();

        Mockito.doNothing()
                .when(mascotaService)
                .transferirPropietario(eq(1L), any());

        mockMvc.perform(patch("/api/v1/mascotas/1/transferir")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());
    }

    @Test
    void actualizarEstadoVital_debeRetornarNoContent() throws Exception {

        Mockito.doNothing()
                .when(mascotaService)
                .actualizarEstadoVital(1L, "FALLECIDO");

        mockMvc.perform(patch("/api/v1/mascotas/1/estado-vital")
                        .param("nuevoEstado", "FALLECIDO"))
                .andExpect(status().isNoContent());
    }

    @Test
    void registrarMascota_invalida_debeRetornarBadRequest() throws Exception {

        MascotaRequestDTO dto = new MascotaRequestDTO();

        mockMvc.perform(post("/api/v1/mascotas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    private MascotaRequestDTO crearRequest() {
        return MascotaRequestDTO.builder()
                .nombre("Firulais")
                .especieNombre("Canino")
                .raza("Labrador")
                .sexo("Macho")
                .fechaNacimiento(LocalDate.of(2020,1,1))
                .idCliente(1L)
                .numeroMicrochip("123")
                .ultimoPeso(12.5)
                .estaEsterilizado(true)
                .alergiasCriticas("Ninguna")
                .build();
    }

    private MascotaResponseDTO crearResponse() {
        return MascotaResponseDTO.builder()
                .idMascota(1L)
                .nombre("Firulais")
                .especie("Canino")
                .raza("Labrador")
                .sexo("Macho")
                .fechaNacimiento(LocalDate.of(2020,1,1))
                .edadCalculada("5 años")
                .idCliente(1L)
                .estado("ACTIVO")
                .numeroHistoriaClinica("HC-001")
                .ultimoPeso(12.5)
                .estaEsterilizado(true)
                .alergiasCriticas("Ninguna")
                .resumenClinico("Sin observaciones")
                .build();
    }
}
