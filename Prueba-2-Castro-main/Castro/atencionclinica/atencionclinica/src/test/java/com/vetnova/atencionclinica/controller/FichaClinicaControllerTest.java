package com.vetnova.atencionclinica.controller;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vetnova.atencionclinica.dto.FichaClinicaRequestDTO;
import com.vetnova.atencionclinica.dto.FichaClinicaResponseDTO;
import com.vetnova.atencionclinica.service.FichaClinicaService;

@WebMvcTest(FichaClinicaController.class)
class FichaClinicaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FichaClinicaService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void crearFicha_debeRetornarCreated() throws Exception {
        FichaClinicaResponseDTO ficha = new FichaClinicaResponseDTO();
        ficha.setIdFicha(1L);
        ficha.setIdMascota(200L);
        ficha.setFechaCreacion(LocalDateTime.now());
        ficha.setObservaciones("Paciente con control inicial");

        Mockito.when(service.crearFicha(any(FichaClinicaRequestDTO.class)))
                .thenReturn(ficha);

        FichaClinicaRequestDTO request = new FichaClinicaRequestDTO();
        request.setIdMascota(200L);
        request.setObservaciones("Paciente con control inicial");

        mockMvc.perform(post("/api/v1/fichas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idFicha").value(1))
                .andExpect(jsonPath("$.idMascota").value(200))
                .andExpect(jsonPath("$.observaciones").value("Paciente con control inicial"));
    }

    @Test
    void crearFicha_conDatosInvalidos_debeRetornarBadRequest() throws Exception {
        FichaClinicaRequestDTO request = new FichaClinicaRequestDTO();

        mockMvc.perform(post("/api/v1/fichas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}