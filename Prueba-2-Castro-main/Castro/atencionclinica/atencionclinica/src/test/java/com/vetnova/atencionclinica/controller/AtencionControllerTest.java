package com.vetnova.atencionclinica.controller;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vetnova.atencionclinica.dto.CertificadoRequestDTO;
import com.vetnova.atencionclinica.dto.DiagnosticoRequestDTO;
import com.vetnova.atencionclinica.dto.DiagnosticoResponseDTO;
import com.vetnova.atencionclinica.dto.RecetaRequestDTO;
import com.vetnova.atencionclinica.dto.TratamientoRequestDTO;
import com.vetnova.atencionclinica.service.DiagnosticoService;

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
        Mockito.when(service.buscarPorId(1L)).thenReturn(crearDiagnosticoResponse());

        mockMvc.perform(get("/api/v1/atenciones/id/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idDiagnostico").value(1))
                .andExpect(jsonPath("$.descripcion").value("Otitis leve"))
                .andExpect(jsonPath("$.idFicha").value(100))
                .andExpect(jsonPath("$.idMascota").value(200));
    }

    @Test
    void registrarDiagnostico_debeRetornarCreated() throws Exception {
        Mockito.when(service.registrarDiagnostico(any(DiagnosticoRequestDTO.class)))
                .thenReturn(crearDiagnosticoResponse());

        mockMvc.perform(post("/api/v1/atenciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(crearRequestValido())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idDiagnostico").value(1))
                .andExpect(jsonPath("$.descripcion").value("Otitis leve"));
    }

    @Test
    void registrarTratamiento_debeRetornarOk() throws Exception {
        DiagnosticoResponseDTO tratamientoResponse = crearDiagnosticoResponse();
        tratamientoResponse.setTratamiento("Antibiótico 7 días");

        Mockito.when(service.registrarTratamiento(eq(1L), eq("Antibiótico 7 días")))
                .thenReturn(tratamientoResponse);

        TratamientoRequestDTO tratamientoRequest = new TratamientoRequestDTO();
        tratamientoRequest.setTratamiento("Antibiótico 7 días");

        mockMvc.perform(put("/api/v1/atenciones/1/tratamiento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tratamientoRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tratamiento").value("Antibiótico 7 días"));
    }

    @Test
    void emitirReceta_debeRetornarOk() throws Exception {
        DiagnosticoResponseDTO recetaResponse = crearDiagnosticoResponse();
        recetaResponse.setRecetaMedica("Receta médica emitida");

        Mockito.when(service.emitirReceta(eq(1L), any(RecetaRequestDTO.class)))
                .thenReturn(recetaResponse);

        RecetaRequestDTO recetaRequest = new RecetaRequestDTO();
        recetaRequest.setRecetaMedica("Receta médica emitida");

        mockMvc.perform(put("/api/v1/atenciones/1/receta")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recetaRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recetaMedica").value("Receta médica emitida"));
    }

    @Test
    void emitirCertificado_debeRetornarOk() throws Exception {
        DiagnosticoResponseDTO certificadoResponse = crearDiagnosticoResponse();
        certificadoResponse.setDetalleCertificado("Certificado emitido");

        Mockito.when(service.emitirCertificado(eq(1L), eq("Certificado emitido")))
                .thenReturn(certificadoResponse);

        CertificadoRequestDTO certificadoRequest = new CertificadoRequestDTO();
        certificadoRequest.setDetalleCertificado("Certificado emitido");

        mockMvc.perform(put("/api/v1/atenciones/1/certificado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(certificadoRequest)))
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

    private DiagnosticoResponseDTO crearDiagnosticoResponse() {
        return DiagnosticoResponseDTO.builder()
                .idDiagnostico(1L)
                .descripcion("Otitis leve")
                .tratamiento("Limpieza y medicamento")
                .recetaMedica("Gotas óticas")
                .detalleCertificado("Certificado simple")
                .fecha(LocalDateTime.now())
                .idVeterinario(10L)
                .idFicha(100L)
                .idMascota(200L)
                .build();
    }

}