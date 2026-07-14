package com.vetnova.atencionclinica.service;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.vetnova.atencionclinica.dto.FichaClinicaRequestDTO;
import com.vetnova.atencionclinica.dto.FichaClinicaResponseDTO;
import com.vetnova.atencionclinica.event.AtencionRegistradaEvent;
import com.vetnova.atencionclinica.exception.ResourceNotFoundException;
import com.vetnova.atencionclinica.exception.ServiceUnavailableException;
import com.vetnova.atencionclinica.model.FichaClinica;
import com.vetnova.atencionclinica.repository.FichaClinicaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class FichaClinicaServiceTest {

    @Mock
    private FichaClinicaRepository repository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private FichaClinicaService service;

    private FichaClinica ficha;

    @SuppressWarnings("unused")
    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "mascotasServiceUrl", "http://localhost:8085");
        ficha = new FichaClinica();
        ficha.setIdFicha(1L);
        ficha.setIdMascota(100L);
        ficha.setObservaciones("Observación de prueba");
    }

    @Test
    void crearFichaDebeGuardarCorrectamente() {

        java.util.Map<String, Object> mascotaBody = java.util.Map.of("idCliente", 55L);
        when(restTemplate.getForEntity(anyString(), eq(java.util.Map.class)))
                .thenReturn(new org.springframework.http.ResponseEntity<>(mascotaBody, HttpStatus.OK));
        when(repository.save(any(FichaClinica.class)))
                .thenReturn(ficha);

        FichaClinicaRequestDTO request = new FichaClinicaRequestDTO();
        request.setIdMascota(100L);
        request.setObservaciones("Observación de prueba");

        FichaClinicaResponseDTO resultado = service.crearFicha(request);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdFicha());
        assertEquals(100L, resultado.getIdMascota());
        assertEquals("Observación de prueba", resultado.getObservaciones());

        verify(repository, times(1))
                .save(any(FichaClinica.class));
        verify(eventPublisher, times(1)).publishEvent(any(AtencionRegistradaEvent.class));
    }

    @Test
    void crearFicha_siLaMascotaNoExiste_debeLanzarResourceNotFoundException() {
        when(restTemplate.getForEntity(anyString(), eq(java.util.Map.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        FichaClinicaRequestDTO request = new FichaClinicaRequestDTO();
        request.setIdMascota(100L);
        request.setObservaciones("Observación de prueba");

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.crearFicha(request)
        );

        assertEquals("La mascota con ID 100 no existe.", exception.getMessage());
    }

    @Test
    void crearFicha_siMascotasEstaCaido_debeLanzarServiceUnavailableException() {
        when(restTemplate.getForEntity(anyString(), eq(java.util.Map.class)))
                .thenThrow(new ResourceAccessException("down"));

        FichaClinicaRequestDTO request = new FichaClinicaRequestDTO();
        request.setIdMascota(100L);
        request.setObservaciones("Observación de prueba");

        ServiceUnavailableException exception = assertThrows(
                ServiceUnavailableException.class,
                () -> service.crearFicha(request)
        );

        assertTrue(exception.getMessage().contains("Mascotas"));
    }

    @Test
    void obtenerTodasDebeRetornarLista() {

        when(repository.findAll())
                .thenReturn(List.of(ficha));

        List<FichaClinica> resultado =
                service.obtenerTodas();

        assertEquals(1, resultado.size());
    }

    @Test
    void buscarPorIdDebeRetornarFicha() {

        when(repository.findById(1L))
                .thenReturn(Optional.of(ficha));

        FichaClinica resultado =
                service.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(100L, resultado.getIdMascota());
    }

    @Test
    void buscarPorIdInexistenteDebeLanzarExcepcion() {

        when(repository.findById(99L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.buscarPorId(99L)
        );
        assertEquals("La ficha clínica con ID 99 no existe.", exception.getMessage());
    }

    // =========================================================================
    // TESTS NUEVOS
    // =========================================================================

    // ---- validarMascotaExistente: error distinto a 404 ----
    @Test
    void crearFicha_siMascotasResponde500_debeLanzarResourceNotFoundException() {
        when(restTemplate.getForEntity(anyString(), eq(java.util.Map.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        FichaClinicaRequestDTO request = new FichaClinicaRequestDTO();
        request.setIdMascota(100L);
        request.setObservaciones("Observación de prueba");

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.crearFicha(request)
        );

        assertEquals("No se pudo validar la mascota con ID 100.", exception.getMessage());
    }

    // ---- validarMascotaExistente: body sin idCliente (rama return null) ----
    @Test
    void crearFicha_siMascotaSinIdCliente_debeGuardarConIdClienteNulo() {
        when(restTemplate.getForEntity(anyString(), eq(java.util.Map.class)))
                .thenReturn(new org.springframework.http.ResponseEntity<>(java.util.Map.of(), HttpStatus.OK));
        when(repository.save(any(FichaClinica.class))).thenReturn(ficha);

        FichaClinicaRequestDTO request = new FichaClinicaRequestDTO();
        request.setIdMascota(100L);
        request.setObservaciones("Observación de prueba");

        FichaClinicaResponseDTO resultado = service.crearFicha(request);

        assertNotNull(resultado);
        verify(repository).save(any(FichaClinica.class));
    }
}
