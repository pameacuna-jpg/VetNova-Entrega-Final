package com.vetnova.atencionclinica.service;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.vetnova.atencionclinica.dto.ConsumoInventarioRequest;
import com.vetnova.atencionclinica.dto.DiagnosticoRequestDTO;
import com.vetnova.atencionclinica.dto.DiagnosticoResponseDTO;
import com.vetnova.atencionclinica.dto.RecetaRequestDTO;
import com.vetnova.atencionclinica.event.CertificadoEmitidoEvent;
import com.vetnova.atencionclinica.event.RecetaEmitidaEvent;
import com.vetnova.atencionclinica.exception.BusinessException;
import com.vetnova.atencionclinica.exception.ResourceNotFoundException;
import com.vetnova.atencionclinica.exception.ServiceUnavailableException;
import com.vetnova.atencionclinica.model.Diagnostico;
import com.vetnova.atencionclinica.model.FichaClinica;
import com.vetnova.atencionclinica.repository.DiagnosticoRepository;

@ExtendWith(MockitoExtension.class)
class DiagnosticoServiceTest {

    @Mock
    private DiagnosticoRepository repository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private DiagnosticoService service;

    private Diagnostico diagnostico;

    @SuppressWarnings("unused")
    @BeforeEach
    void setUp() {

        ReflectionTestUtils.setField(service, "inventarioServiceUrl", "http://localhost:8087");

        FichaClinica ficha = new FichaClinica();
        ficha.setIdFicha(1L);
        ficha.setIdMascota(100L);
        ficha.setIdCliente(55L);

        diagnostico = new Diagnostico();
        diagnostico.setIdDiagnostico(1L);
        diagnostico.setIdVeterinario(10L);
        diagnostico.setDescripcion("Otitis");
        diagnostico.setFichaClinica(ficha);
    }

    @Test
    void buscarPorIdDebeRetornarDiagnostico() {

        when(repository.findById(1L))
                .thenReturn(Optional.of(diagnostico));

        DiagnosticoResponseDTO resultado =
                service.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdDiagnostico());
        assertEquals("Otitis", resultado.getDescripcion());
    }

    @Test
    void buscarPorIdInexistenteDebeLanzarExcepcion() {

        when(repository.findById(99L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.buscarPorId(99L)
        );
        assertEquals("La atención con ID 99 no existe.", exception.getMessage());
    }

    @Test
    void registrarDiagnosticoDebeGuardarCorrectamente() {

        when(repository.save(any(Diagnostico.class)))
                .thenReturn(diagnostico);

        DiagnosticoRequestDTO request = new DiagnosticoRequestDTO();
        request.setDescripcion("Otitis");
        request.setIdVeterinario(10L);
        request.setIdFicha(1L);

        DiagnosticoResponseDTO resultado =
                service.registrarDiagnostico(request);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdDiagnostico());

        verify(repository)
                .save(any(Diagnostico.class));
    }

    @Test
    void registrarTratamientoDebeActualizarDiagnostico() {

        when(repository.findById(1L))
                .thenReturn(Optional.of(diagnostico));

        when(repository.save(any(Diagnostico.class)))
                .thenReturn(diagnostico);

        DiagnosticoResponseDTO resultado =
                service.registrarTratamiento(
                        1L,
                        "Antibiótico por 7 días"
                );

        assertNotNull(resultado);
        assertEquals("Otitis", resultado.getDescripcion());

        verify(repository)
                .save(any(Diagnostico.class));
    }

    @Test
    void emitirRecetaSinProductoDebePublicarEventoYNoLlamarInventario() {

        when(repository.findById(1L))
                .thenReturn(Optional.of(diagnostico));

        when(repository.save(any(Diagnostico.class)))
                .thenReturn(diagnostico);

        RecetaRequestDTO request = new RecetaRequestDTO();
        request.setRecetaMedica("Amoxicilina 500mg");

        DiagnosticoResponseDTO resultado = service.emitirReceta(1L, request);

        assertNotNull(resultado);
        assertEquals("Amoxicilina 500mg", resultado.getRecetaMedica());

        ArgumentCaptor<RecetaEmitidaEvent> captor =
                ArgumentCaptor.forClass(RecetaEmitidaEvent.class);

        verify(eventPublisher)
                .publishEvent(captor.capture());

        assertNotNull(captor.getValue());
        assertEquals(100L, captor.getValue().getIdMascota());
        assertEquals(55L, captor.getValue().getIdCliente());

        verify(restTemplate, never()).getForEntity(anyString(), eq(Map.class));
        verify(restTemplate, never()).postForEntity(anyString(), any(), eq(Object.class));
    }

    @Test
    void emitirRecetaConProductoDisponibleDebeConsumirInventarioYPublicarEvento() {

        when(repository.findById(1L))
                .thenReturn(Optional.of(diagnostico));

        when(repository.save(any(Diagnostico.class)))
                .thenReturn(diagnostico);

        when(restTemplate.getForEntity(anyString(), eq(Map.class)))
                .thenReturn(new org.springframework.http.ResponseEntity<>(
                        Map.of("disponible", true, "activo", true, "mensaje", "Producto disponible"), HttpStatus.OK));

        RecetaRequestDTO request = new RecetaRequestDTO();
        request.setRecetaMedica("Amoxicilina 500mg");
        request.setIdProducto(7L);
        request.setCantidad(2);

        DiagnosticoResponseDTO resultado = service.emitirReceta(1L, request);

        assertNotNull(resultado);

        ArgumentCaptor<ConsumoInventarioRequest> captor = ArgumentCaptor.forClass(ConsumoInventarioRequest.class);
        verify(restTemplate).postForEntity(eq("http://localhost:8087/api/v1/inventario/consumos"), captor.capture(), eq(Object.class));

        ConsumoInventarioRequest consumo = captor.getValue();
        assertEquals(7L, consumo.getIdProducto());
        assertEquals(2, consumo.getCantidad());
        assertEquals("ATENCION_CLINICA", consumo.getOrigen());
        assertEquals(1L, consumo.getIdReferencia());

        verify(eventPublisher).publishEvent(any(RecetaEmitidaEvent.class));
    }

    @Test
    void emitirRecetaConStockInsuficienteDebeLanzarBusinessExceptionYNoPublicarEvento() {

        when(repository.findById(1L))
                .thenReturn(Optional.of(diagnostico));

        when(repository.save(any(Diagnostico.class)))
                .thenReturn(diagnostico);

        when(restTemplate.getForEntity(anyString(), eq(Map.class)))
                .thenReturn(new org.springframework.http.ResponseEntity<>(
                        Map.of("disponible", false, "activo", true, "mensaje", "Stock insuficiente"), HttpStatus.OK));

        RecetaRequestDTO request = new RecetaRequestDTO();
        request.setRecetaMedica("Amoxicilina 500mg");
        request.setIdProducto(7L);
        request.setCantidad(50);

        BusinessException ex = assertThrows(BusinessException.class, () -> service.emitirReceta(1L, request));

        assertEquals("Stock insuficiente", ex.getMessage());
        verify(restTemplate, never()).postForEntity(anyString(), any(), eq(Object.class));
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void emitirRecetaConInventarioCaidoDebeLanzarServiceUnavailableException() {

        when(repository.findById(1L))
                .thenReturn(Optional.of(diagnostico));

        when(repository.save(any(Diagnostico.class)))
                .thenReturn(diagnostico);

        when(restTemplate.getForEntity(anyString(), eq(Map.class)))
                .thenThrow(new ResourceAccessException("down"));

        RecetaRequestDTO request = new RecetaRequestDTO();
        request.setRecetaMedica("Amoxicilina 500mg");
        request.setIdProducto(7L);
        request.setCantidad(2);

        assertThrows(ServiceUnavailableException.class, () -> service.emitirReceta(1L, request));

        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void emitirCertificadoDebePublicarEvento() {

        when(repository.findById(1L))
                .thenReturn(Optional.of(diagnostico));

        when(repository.save(any(Diagnostico.class)))
                .thenReturn(diagnostico);

        DiagnosticoResponseDTO resultado = service.emitirCertificado(
                1L,
                "Reposo por 5 días"
        );

        assertNotNull(resultado);
        assertEquals("Reposo por 5 días", resultado.getDetalleCertificado());

        ArgumentCaptor<CertificadoEmitidoEvent> captor =
                ArgumentCaptor.forClass(CertificadoEmitidoEvent.class);

        verify(eventPublisher)
                .publishEvent(captor.capture());

        assertNotNull(captor.getValue());
        assertEquals(100L, captor.getValue().getIdMascota());
        assertEquals(55L, captor.getValue().getIdCliente());
    }
}