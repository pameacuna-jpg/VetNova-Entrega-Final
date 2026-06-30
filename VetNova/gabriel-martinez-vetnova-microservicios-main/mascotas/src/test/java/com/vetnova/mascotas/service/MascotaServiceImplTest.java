package com.vetnova.mascotas.service;

import com.vetnova.mascotas.dto.ClienteDTO;
import com.vetnova.mascotas.dto.MascotaRequestDTO;
import com.vetnova.mascotas.dto.MascotaResponseDTO;
import com.vetnova.mascotas.dto.TransferenciaRequestDTO;
import com.vetnova.mascotas.exception.ResourceNotFoundException;
import com.vetnova.mascotas.model.Especie;
import com.vetnova.mascotas.model.HistorialMascota;
import com.vetnova.mascotas.model.Mascota;
import com.vetnova.mascotas.repository.EspecieRepository;
import com.vetnova.mascotas.repository.MascotaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MascotaServiceImplTest {

    @Mock
    private MascotaRepository mascotaRepository;

    @Mock
    private EspecieRepository especieRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private MascotaServiceImpl mascotaService;

    private Mascota mascota;
    private Especie especie;
    private MascotaRequestDTO request;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(
                mascotaService,
                "clientesUrl",
                "http://localhost:8084/api/v1/clientes"
        );

        ReflectionTestUtils.setField(
                mascotaService,
                "notificacionesUrl",
                "http://localhost:8089/api/v1/notificaciones"
        );

        especie = new Especie();
        especie.setIdEspecie(1L);
        especie.setNombre("Canino");

        HistorialMascota historial = HistorialMascota.builder()
                .idHistorial(1L)
                .numeroHistoriaClinica("HC-TEST")
                .resumenClinico("Ficha clínica inicial creada automáticamente")
                .ultimoPeso(12.5)
                .estaEsterilizado(true)
                .alergiasCriticas("Ninguna")
                .build();

        mascota = Mascota.builder()
                .idMascota(1L)
                .nombre("Luna")
                .especie(especie)
                .raza("Labrador")
                .sexo("Hembra")
                .fechaNacimiento(LocalDate.now().minusYears(2))
                .idCliente(50L)
                .estado("ACTIVO")
                .numeroMicrochip("MIC123")
                .build();

        mascota.asignarHistorial(historial);

        request = MascotaRequestDTO.builder()
                .nombre("Luna")
                .especieNombre("Canino")
                .raza("Labrador")
                .sexo("Hembra")
                .fechaNacimiento(LocalDate.now().minusYears(2))
                .idCliente(50L)
                .numeroMicrochip("MIC123")
                .ultimoPeso(12.5)
                .estaEsterilizado(true)
                .alergiasCriticas("Ninguna")
                .build();
    }

    @Test
    void registrar_cuandoEspecieYClienteExisten_debeCrearMascotaActivaConHistorialClinico() {
        ClienteDTO cliente = new ClienteDTO();
        cliente.setIdCliente(50L);
        cliente.setNombre("Cliente Test");

        when(especieRepository.findByNombreIgnoreCase("Canino"))
                .thenReturn(Optional.of(especie));

        when(restTemplate.getForObject(
                eq("http://localhost:8084/api/v1/clientes/50"),
                eq(ClienteDTO.class)
        )).thenReturn(cliente);

        when(mascotaRepository.save(any(Mascota.class)))
                .thenReturn(mascota);

        MascotaResponseDTO resultado = mascotaService.registrar(request);

        assertNotNull(resultado);
        assertEquals("Luna", resultado.getNombre());
        assertEquals("Canino", resultado.getEspecie());
        assertEquals("ACTIVO", resultado.getEstado());
        assertEquals(50L, resultado.getIdCliente());
        assertNotNull(resultado.getNumeroHistoriaClinica());

        verify(especieRepository).findByNombreIgnoreCase("Canino");
        verify(restTemplate).getForObject(
                eq("http://localhost:8084/api/v1/clientes/50"),
                eq(ClienteDTO.class)
        );
        verify(mascotaRepository).save(any(Mascota.class));
    }

    @Test
    void registrar_cuandoClienteNoExiste_debeLanzarExcepcion() {
        when(especieRepository.findByNombreIgnoreCase("Canino"))
                .thenReturn(Optional.of(especie));

        when(restTemplate.getForObject(
                eq("http://localhost:8084/api/v1/clientes/50"),
                eq(ClienteDTO.class)
        )).thenReturn(null);

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> mascotaService.registrar(request)
        );

        assertTrue(ex.getMessage().contains("Cliente propietario no existe"));
        verify(mascotaRepository, never()).save(any(Mascota.class));
    }

    @Test
    void obtenerPorId_cuandoMascotaExiste_debeRetornarDetalle() {
        when(mascotaRepository.findById(1L))
                .thenReturn(Optional.of(mascota));

        MascotaResponseDTO resultado = mascotaService.obtenerPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdMascota());
        assertEquals("Luna", resultado.getNombre());
        assertEquals("Canino", resultado.getEspecie());
        assertEquals(true, resultado.getEstaEsterilizado());

        verify(mascotaRepository).findById(1L);
    }

    @Test
    void obtenerPorId_cuandoMascotaNoExiste_debeLanzarExcepcion() {
        when(mascotaRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> mascotaService.obtenerPorId(99L)
        );

        verify(mascotaRepository).findById(99L);
    }

    @Test
    void actualizar_cuandoMascotaExiste_debeActualizarDatos() {
        MascotaRequestDTO datosActualizados = MascotaRequestDTO.builder()
                .nombre("Max")
                .especieNombre("Canino")
                .raza("Beagle")
                .sexo("Macho")
                .fechaNacimiento(LocalDate.now().minusYears(3))
                .idCliente(50L)
                .numeroMicrochip("MIC999")
                .ultimoPeso(10.0)
                .estaEsterilizado(false)
                .alergiasCriticas("Polen")
                .build();

        when(mascotaRepository.findById(1L))
                .thenReturn(Optional.of(mascota));

        when(especieRepository.findByNombreIgnoreCase("Canino"))
                .thenReturn(Optional.of(especie));

        when(mascotaRepository.save(any(Mascota.class)))
                .thenReturn(mascota);

        MascotaResponseDTO resultado = mascotaService.actualizar(1L, datosActualizados);

        assertNotNull(resultado);
        assertEquals("Max", resultado.getNombre());
        assertEquals("Beagle", resultado.getRaza());
        assertEquals("Macho", resultado.getSexo());

        verify(mascotaRepository).findById(1L);
        verify(especieRepository).findByNombreIgnoreCase("Canino");
        verify(mascotaRepository).save(any(Mascota.class));
    }

    @Test
    void buscarPacientes_debeRetornarPaginaMapeada() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Mascota> page = new PageImpl<>(List.of(mascota), pageable, 1);

        when(mascotaRepository.findAll(pageable))
                .thenReturn(page);

        Page<MascotaResponseDTO> resultado =
                mascotaService.buscarPacientes("Luna", pageable);

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        assertEquals("Luna", resultado.getContent().get(0).getNombre());

        verify(mascotaRepository).findAll(pageable);
    }

    @Test
    void transferirPropietario_cuandoMascotaExiste_debeCambiarClienteYNotificar() {
        TransferenciaRequestDTO transferencia = new TransferenciaRequestDTO();
        transferencia.setIdNuevoCliente(99L);

        ClienteDTO clienteNuevo = new ClienteDTO();
        clienteNuevo.setIdCliente(99L);
        clienteNuevo.setNombre("Cliente Nuevo");

        when(mascotaRepository.findById(1L))
                .thenReturn(Optional.of(mascota));

        when(restTemplate.getForObject(
                eq("http://localhost:8084/api/v1/clientes/99"),
                eq(ClienteDTO.class)
        )).thenReturn(clienteNuevo);

        when(mascotaRepository.save(any(Mascota.class)))
                .thenReturn(mascota);

        mascotaService.transferirPropietario(1L, transferencia);

        assertEquals(99L, mascota.getIdCliente());

        verify(mascotaRepository).findById(1L);
        verify(restTemplate).getForObject(
                eq("http://localhost:8084/api/v1/clientes/99"),
                eq(ClienteDTO.class)
        );
        verify(mascotaRepository).save(mascota);
    }

    @Test
    void actualizarEstadoVital_cuandoMascotaExiste_debeGuardarEstadoEnMayusculas() {
        when(mascotaRepository.findById(1L))
                .thenReturn(Optional.of(mascota));

        when(mascotaRepository.save(any(Mascota.class)))
                .thenReturn(mascota);

        mascotaService.actualizarEstadoVital(1L, "fallecido");

        assertEquals("FALLECIDO", mascota.getEstado());

        verify(mascotaRepository).findById(1L);
        verify(mascotaRepository).save(mascota);
    }
}