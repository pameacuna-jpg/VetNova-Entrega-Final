package com.vetnova.clientes.service;

import com.vetnova.clientes.dto.ClienteRequestDTO;
import com.vetnova.clientes.dto.ClienteResponseDTO;
import com.vetnova.clientes.dto.ContactoEmergenciaDTO;
import com.vetnova.clientes.exception.ResourceNotFoundException;
import com.vetnova.clientes.model.Cliente;
import com.vetnova.clientes.model.HistorialCliente;
import com.vetnova.clientes.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceImplTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteServiceImpl clienteService;

    private ClienteRequestDTO request;

    @BeforeEach
    void setUp() {
        request = ClienteRequestDTO.builder()
                .rut("12.345.678-9")
                .nombre("Cliente VetNova")
                .telefono("+56912345678")
                .email("cliente@vetnova.cl")
                .direccion("Av. Siempre Viva 123")
                .contactos(List.of(ContactoEmergenciaDTO.builder()
                        .nombre("Contacto Emergencia")
                        .telefono("+56911111111")
                        .parentesco("Hermana")
                        .build()))
                .build();
    }

    @Test
    void registrar_cuandoRutNoExiste_debeCrearClienteActivoConHistorialYContactos() {
        // Given
        when(clienteRepository.existsByRut("12.345.678-9")).thenReturn(false);
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocation -> {
            Cliente cliente = invocation.getArgument(0);
            cliente.setIdCliente(1L);
            cliente.getHistorialCliente().setIdHistorial(10L);
            return cliente;
        });

        // When
        ClienteResponseDTO respuesta = clienteService.registrar(request);

        // Then
        assertNotNull(respuesta);
        assertEquals(1L, respuesta.getIdCliente());
        assertEquals("12.345.678-9", respuesta.getRut());
        assertEquals("Cliente VetNova", respuesta.getNombre());
        assertEquals("ACTIVO", respuesta.getEstado());
        assertEquals(1, respuesta.getContactos().size());
        assertNotNull(respuesta.getHistorial());
        assertEquals(0, respuesta.getHistorial().getTotalCompras());
        assertEquals(0, respuesta.getHistorial().getTotalAtenciones());

        ArgumentCaptor<Cliente> captor = ArgumentCaptor.forClass(Cliente.class);
        verify(clienteRepository).save(captor.capture());
        Cliente clienteGuardado = captor.getValue();
        assertNotNull(clienteGuardado.getFechaCreacion());
        assertSame(clienteGuardado, clienteGuardado.getHistorialCliente().getCliente());
        assertSame(clienteGuardado, clienteGuardado.getContactosEmergencia().get(0).getCliente());
    }

    @Test
    void registrar_cuandoRutYaExiste_debeLanzarIllegalArgumentException() {
        // Given
        when(clienteRepository.existsByRut("12.345.678-9")).thenReturn(true);

        // When / Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> clienteService.registrar(request));

        assertEquals("El RUT ya se encuentra registrado", exception.getMessage());
        verify(clienteRepository, never()).save(any());
    }

    @Test
    void actualizarDatos_cuandoClienteExiste_debeActualizarDatosBasicos() {
        // Given
        Cliente clienteExistente = crearClienteBase();
        ClienteRequestDTO actualizacion = ClienteRequestDTO.builder()
                .rut("12.345.678-9")
                .nombre("Cliente Actualizado")
                .telefono("+56999999999")
                .email("actualizado@vetnova.cl")
                .direccion("Nueva dirección")
                .build();

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteExistente));
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        ClienteResponseDTO respuesta = clienteService.actualizarDatos(1L, actualizacion);

        // Then
        assertEquals("Cliente Actualizado", respuesta.getNombre());
        assertEquals("+56999999999", respuesta.getTelefono());
        assertEquals("actualizado@vetnova.cl", respuesta.getEmail());
        assertEquals("Nueva dirección", respuesta.getDireccion());
        assertNotNull(clienteExistente.getFechaUltimaModificacion());
        verify(clienteRepository).save(clienteExistente);
    }

    @Test
    void obtenerDetalleCliente_cuandoClienteNoExiste_debeLanzarResourceNotFoundException() {
        // Given
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> clienteService.obtenerDetalleCliente(99L));

        assertEquals("Cliente no encontrado", exception.getMessage());
    }

    @Test
    void buscarClientes_cuandoFiltroTieneMenosDeTresCaracteres_debeLanzarIllegalArgumentException() {
        // When / Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> clienteService.buscarClientes("ab", PageRequest.of(0, 10)));

        assertEquals("La búsqueda requiere un mínimo de 3 caracteres", exception.getMessage());
        verify(clienteRepository, never()).buscarPorFiltros(anyString(), any(Pageable.class));
    }

    @Test
    void buscarClientes_cuandoFiltroValido_debeRetornarPaginaMapeada() {
        // Given
        Cliente cliente = crearClienteBase();
        Pageable pageable = PageRequest.of(0, 10);
        when(clienteRepository.buscarPorFiltros("cli", pageable))
                .thenReturn(new PageImpl<>(List.of(cliente), pageable, 1));

        // When
        Page<ClienteResponseDTO> resultado = clienteService.buscarClientes("cli", pageable);

        // Then
        assertEquals(1, resultado.getTotalElements());
        assertEquals("Cliente VetNova", resultado.getContent().get(0).getNombre());
        verify(clienteRepository).buscarPorFiltros("cli", pageable);
    }

    @Test
    void eliminar_cuandoClienteExiste_debeAnonimizarYDesactivar() {
        // Given
        Cliente cliente = crearClienteBase();
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        // When
        clienteService.eliminar(1L);

        // Then
        assertEquals("INACTIVO", cliente.getEstado());
        assertTrue(cliente.getNombre().startsWith("ANON_"));
        assertTrue(cliente.getRut().startsWith("ANON_"));
        assertEquals("+000000000", cliente.getTelefono());
        assertTrue(cliente.getEmail().startsWith("anonimo_"));
        assertEquals("ANONIMIZADO", cliente.getDireccion());
        assertNotNull(cliente.getFechaUltimaModificacion());
        verify(clienteRepository).save(cliente);
    }

    private Cliente crearClienteBase() {
        HistorialCliente historial = HistorialCliente.builder()
                .idHistorial(10L)
                .totalCompras(2)
                .totalAtenciones(3)
                .build();

        Cliente cliente = Cliente.builder()
                .idCliente(1L)
                .rut("12.345.678-9")
                .nombre("Cliente VetNova")
                .telefono("+56912345678")
                .email("cliente@vetnova.cl")
                .direccion("Av. Siempre Viva 123")
                .estado("ACTIVO")
                .fechaCreacion(LocalDateTime.now())
                .build();

        cliente.asignarHistorial(historial);
        return cliente;
    }
}
