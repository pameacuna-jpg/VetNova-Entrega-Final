package com.vetnova.atencionclinica.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vetnova.atencionclinica.dto.DiagnosticoRequestDTO;
import com.vetnova.atencionclinica.model.Diagnostico;
import com.vetnova.atencionclinica.model.FichaClinica;
import com.vetnova.atencionclinica.service.DiagnosticoService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AtencionController.class)
class AtencionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DiagnosticoService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void buscarAtencionPorId_debeRetornarOk() throws Exception {
        Mockito.when(service.buscarPorId(1L)).thenReturn(crearDiagnostico());

        mockMvc.perform(get("/api/v1/atenciones/id/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idDiagnostico").value(1))
                .andExpect(jsonPath("$.descripcion").value("Otitis leve"))
                .andExpect(jsonPath("$.idFicha").value(100))
                .andExpect(jsonPath("$.idMascota").value(200));
    }

    @Test
    void registrarDiagnostico_debeRetornarCreated() throws Exception {
        Mockito.when(service.registrarDiagnostico(any(Diagnostico.class)))
                .thenReturn(crearDiagnostico());

        mockMvc.perform(post("/api/v1/atenciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(crearRequestValido())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idDiagnostico").value(1))
                .andExpect(jsonPath("$.descripcion").value("Otitis leve"));
    }

    @Test
    void registrarTratamiento_debeRetornarOk() throws Exception {
        Diagnostico diagnostico = crearDiagnostico();
        diagnostico.setTratamiento("Antibiótico 7 días");

        Mockito.when(service.registrarTratamiento(eq(1L), eq("Antibiótico 7 días")))
                .thenReturn(diagnostico);

        mockMvc.perform(put("/api/v1/atenciones/1/tratamiento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Map.of("tratamiento", "Antibiótico 7 días")
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tratamiento").value("Antibiótico 7 días"));
    }

    @Test
    void emitirReceta_debeRetornarOk() throws Exception {
        Diagnostico diagnostico = crearDiagnostico();
        diagnostico.setRecetaMedica("Receta médica emitida");

        Mockito.when(service.emitirReceta(eq(1L), eq("Receta médica emitida")))
                .thenReturn(diagnostico);

        mockMvc.perform(put("/api/v1/atenciones/1/receta")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Map.of("recetaMedica", "Receta médica emitida")
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recetaMedica").value("Receta médica emitida"));
    }

    @Test
    void emitirCertificado_debeRetornarOk() throws Exception {
        Diagnostico diagnostico = crearDiagnostico();
        diagnostico.setDetalleCertificado("Certificado emitido");

        Mockito.when(service.emitirCertificado(eq(1L), eq("Certificado emitido")))
                .thenReturn(diagnostico);

        mockMvc.perform(put("/api/v1/atenciones/1/certificado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Map.of("detalleCertificado", "Certificado emitido")
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.detalleCertificado").value("Certificado emitido"));
    }

    @Test
    void registrarDiagnostico_conDatosInvalidos_debeRetornarBadRequest() throws Exception {
        DiagnosticoRequestDTO request = new DiagnosticoRequestDTO();

        mockMvc.perform(post("/api/v1/atenciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    private DiagnosticoRequestDTO crearRequestValido() {
        DiagnosticoRequestDTO request = new DiagnosticoRequestDTO();
        request.setDescripcion("Otitis leve");
        request.setTratamiento("Limpieza y medicamento");
        request.setRecetaMedica("Gotas óticas");
        request.setDetalleCertificado("Certificado simple");
        request.setIdVeterinario(10L);
        request.setIdFicha(100L);
        return request;
    }

    private Diagnostico crearDiagnostico() {
        FichaClinica ficha = new FichaClinica();
        ficha.setIdFicha(100L);
        ficha.setIdMascota(200L);

        Diagnostico diagnostico = new Diagnostico();
        diagnostico.setIdDiagnostico(1L);
        diagnostico.setDescripcion("Otitis leve");
        diagnostico.setTratamiento("Limpieza y medicamento");
        diagnostico.setRecetaMedica("Gotas óticas");
        diagnostico.setDetalleCertificado("Certificado simple");
        diagnostico.setFecha(LocalDateTime.now());
        diagnostico.setIdVeterinario(10L);
        diagnostico.setFichaClinica(ficha);

        return diagnostico;
    }
}