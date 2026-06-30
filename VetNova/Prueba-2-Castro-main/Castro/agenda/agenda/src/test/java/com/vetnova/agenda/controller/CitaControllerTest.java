package com.vetnova.agenda.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vetnova.agenda.dto.CitaRequestDTO;
import com.vetnova.agenda.model.Cita;
import com.vetnova.agenda.service.CitaService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CitaController.class)
class CitaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CitaService citaService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void agendarHora_debeRetornarCreated() throws Exception {
        CitaRequestDTO request = crearRequestValido();
        Cita response = crearCita();

        Mockito.when(citaService.agendarHora(any(Cita.class)))
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
        Cita response = crearCita();
        response.setEstado("REPROGRAMADA");
        response.setFechaHora(LocalDateTime.now().plusDays(2));

        Mockito.when(citaService.reprogramarHora(eq(1L), any(LocalDateTime.class)))
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
        Cita response = crearCita();
        response.setEstado("CANCELADA");

        Mockito.when(citaService.cancelarHora(1L))
                .thenReturn(response);

        mockMvc.perform(put("/api/v1/citas/1/cancelar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CANCELADA"));
    }

    @Test
    void confirmarAsistencia_debeRetornarOk() throws Exception {
        Cita response = crearCita();
        response.setEstado("CONFIRMADA");

        Mockito.when(citaService.confirmarAsistencia(1L))
                .thenReturn(response);

        mockMvc.perform(put("/api/v1/citas/1/confirmar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CONFIRMADA"));
    }

    @Test
    void listarCitas_debeRetornarLista() throws Exception {
        Mockito.when(citaService.obtenerTodasLasCitas())
                .thenReturn(List.of(crearCita()));

        mockMvc.perform(get("/api/v1/citas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].estado").value("AGENDADA"));
    }

    @Test
    void obtenerCita_debeRetornarCita() throws Exception {
        Mockito.when(citaService.obtenerCitaPorId(1L))
                .thenReturn(crearCita());

        mockMvc.perform(get("/api/v1/citas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.motivo").value("Consulta general"));
    }

    @Test
    void obtenerCitasDeManana_debeRetornarLista() throws Exception {
        Mockito.when(citaService.obtenerCitasProximas24h())
                .thenReturn(List.of(crearCita()));

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

    private Cita crearCita() {
        Cita cita = new Cita();
        cita.setId(1L);
        cita.setIdCliente(10L);
        cita.setIdMascota(20L);
        cita.setIdVeterinario(30L);
        cita.setFechaHora(LocalDateTime.now().plusDays(1));
        cita.setMotivo("Consulta general");
        cita.setEstado("AGENDADA");
        return cita;
    }
}