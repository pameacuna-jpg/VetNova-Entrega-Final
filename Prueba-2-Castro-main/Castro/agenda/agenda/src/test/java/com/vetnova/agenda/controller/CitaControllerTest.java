package com.vetnova.agenda.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.vetnova.agenda.dto.CitaRequestDTO;
import com.vetnova.agenda.dto.CitaResponseDTO;
import com.vetnova.agenda.service.CitaService;

@ExtendWith(MockitoExtension.class)
class CitaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CitaService citaService;

    @InjectMocks
    private CitaController citaController;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @SuppressWarnings("unused")
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(citaController).build();
    }

    @Test
    void agendarHora_debeRetornarCreated() throws Exception {
        CitaRequestDTO request = crearRequestValido();
        CitaResponseDTO response = crearRespuesta();

        when(citaService.agendarHora(any(CitaRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/citas/agendar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.idCliente").value(10))
                .andExpect(jsonPath("$.idMascota").value(20))
                .andExpect(jsonPath("$.estado").value("AGENDADA"));
    }

    @Test
    void reprogramarHora_debeRetornarOk() throws Exception {
        CitaResponseDTO response = crearRespuesta();
        response.setEstado("REPROGRAMADA");
        response.setFechaHora(LocalDateTime.now().plusDays(2));

        when(citaService.reprogramarHora(eq(1L), any(LocalDateTime.class)))
                .thenReturn(response);

        Map<String, String> body = Map.of(
                "nuevaFechaHora",
                LocalDateTime.now().plusDays(2).toString()
        );

        mockMvc.perform(put("/api/v1/citas/1/reprogramar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("REPROGRAMADA"));
    }

    @Test
    void cancelarHora_debeRetornarOk() throws Exception {
        CitaResponseDTO response = crearRespuesta();
        response.setEstado("CANCELADA");

        when(citaService.cancelarHora(1L))
                .thenReturn(response);

        mockMvc.perform(put("/api/v1/citas/1/cancelar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CANCELADA"));
    }

    @Test
    void confirmarAsistencia_debeRetornarOk() throws Exception {
        CitaResponseDTO response = crearRespuesta();
        response.setEstado("CONFIRMADA");

        when(citaService.confirmarAsistencia(1L))
                .thenReturn(response);

        mockMvc.perform(put("/api/v1/citas/1/confirmar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CONFIRMADA"));
    }

    @Test
    void listarCitas_debeRetornarLista() throws Exception {
        when(citaService.obtenerTodasLasCitas())
                .thenReturn(List.of(crearRespuesta()));

        mockMvc.perform(get("/api/v1/citas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].estado").value("AGENDADA"));
    }

    @Test
    void obtenerCita_debeRetornarCita() throws Exception {
        when(citaService.obtenerCitaPorId(1L))
                .thenReturn(crearRespuesta());

        mockMvc.perform(get("/api/v1/citas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.motivo").value("Consulta general"));
    }

    @Test
    void obtenerCitasDeManana_debeRetornarLista() throws Exception {
        when(citaService.obtenerCitasProximas24h())
                .thenReturn(List.of(crearRespuesta()));

        mockMvc.perform(get("/api/v1/citas/manana"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idCliente").value(10));
    }

    @Test
    void agendarHora_conDatosInvalidos_debeRetornarBadRequest() throws Exception {
        CitaRequestDTO request = new CitaRequestDTO();
        request.setIdCliente(null);
        request.setIdMascota(null);
        request.setIdVeterinario(null);
        request.setFechaHora(LocalDateTime.now().minusDays(1));
        request.setMotivo("");

        mockMvc.perform(post("/api/v1/citas/agendar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    private CitaRequestDTO crearRequestValido() {
        CitaRequestDTO request = new CitaRequestDTO();
        request.setIdCliente(10L);
        request.setIdMascota(20L);
        request.setIdVeterinario(30L);
        request.setFechaHora(LocalDateTime.now().plusDays(1));
        request.setMotivo("Consulta general");
        return request;
    }

    private CitaResponseDTO crearRespuesta() {
        CitaResponseDTO response = new CitaResponseDTO();
        response.setId(1L);
        response.setIdCliente(10L);
        response.setIdMascota(20L);
        response.setIdVeterinario(30L);
        response.setFechaHora(LocalDateTime.now().plusDays(1));
        response.setMotivo("Consulta general");
        response.setEstado("AGENDADA");
        return response;
    }
}