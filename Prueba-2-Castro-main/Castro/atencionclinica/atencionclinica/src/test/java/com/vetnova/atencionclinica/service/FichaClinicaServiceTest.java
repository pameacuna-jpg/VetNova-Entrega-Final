package com.vetnova.atencionclinica.service;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import com.vetnova.atencionclinica.model.FichaClinica;
import com.vetnova.atencionclinica.repository.FichaClinicaRepository;

@ExtendWith(MockitoExtension.class)
class FichaClinicaServiceTest {

    @Mock
    private FichaClinicaRepository repository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private FichaClinicaService service;

    private FichaClinica ficha;

    @SuppressWarnings("unused")
    @BeforeEach
    void setUp() {
        ficha = new FichaClinica();
        ficha.setIdFicha(1L);
        ficha.setIdMascota(100L);
        ficha.setObservaciones("Observación de prueba");
    }

    @Test
    void crearFichaDebeGuardarCorrectamente() {

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
}