package com.vetnova.usuarios.service;

import com.vetnova.usuarios.dto.UsuarioRequestDTO;
import com.vetnova.usuarios.dto.UsuarioResponseDTO;
import com.vetnova.usuarios.exception.ResourceNotFoundException;
import com.vetnova.usuarios.model.Rol;
import com.vetnova.usuarios.model.Usuario;
import com.vetnova.usuarios.repository.RolRepository;
import com.vetnova.usuarios.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RolRepository rolRepository;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    private Rol rolAdministrador;
    private UsuarioRequestDTO request;

    @BeforeEach
    void setUp() {
        rolAdministrador = Rol.builder()
                .idRol(1L)
                .nombreRol("ADMIN")
                .descripcion("Administrador del sistema")
                .build();

        request = UsuarioRequestDTO.builder()
                .nombre("Gabriel Martinez")
                .email("gabriel@vetnova.cl")
                .password("123456")
                .idSucursal(10L)
                .idsRoles(Set.of(1L))
                .build();
    }

    @Test
    void registrarUsuario_cuandoEmailNoExiste_debeGuardarUsuarioConRolYEstadoPendiente() {
        // Given
        when(usuarioRepository.existsByEmail("gabriel@vetnova.cl")).thenReturn(false);
        when(rolRepository.findById(1L)).thenReturn(Optional.of(rolAdministrador));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario usuario = invocation.getArgument(0);
            usuario.setIdUsuario(100L);
            return usuario;
        });

        // When
        UsuarioResponseDTO respuesta = usuarioService.registrarUsuario(request);

        // Then
        assertNotNull(respuesta);
        assertEquals(100L, respuesta.getIdUsuario());
        assertEquals("Gabriel Martinez", respuesta.getNombre());
        assertEquals("gabriel@vetnova.cl", respuesta.getEmail());
        assertEquals("PENDIENTE_ACTIVACION", respuesta.getEstado());
        assertEquals(10L, respuesta.getIdSucursal());
        assertTrue(respuesta.getRoles().contains("ADMIN"));

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        Usuario usuarioGuardado = captor.getValue();
        assertEquals("$2a$10$SimulatedBcryptHash...", usuarioGuardado.getPassword());
        assertEquals(1, usuarioGuardado.getRolesAsignados().size());
        assertSame(usuarioGuardado, usuarioGuardado.getRolesAsignados().get(0).getUsuario());
        assertEquals("ADMIN", usuarioGuardado.getRolesAsignados().get(0).getRol().getNombreRol());
    }

    @Test
    void registrarUsuario_cuandoEmailYaExiste_debeLanzarIllegalArgumentException() {
        // Given
        when(usuarioRepository.existsByEmail("gabriel@vetnova.cl")).thenReturn(true);

        // When / Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> usuarioService.registrarUsuario(request));

        assertEquals("El email ya se encuentra registrado", exception.getMessage());
        verify(usuarioRepository, never()).save(any());
        verify(rolRepository, never()).findById(anyLong());
    }

    @Test
    void registrarUsuario_cuandoRolNoExiste_debeLanzarResourceNotFoundException() {
        // Given
        when(usuarioRepository.existsByEmail("gabriel@vetnova.cl")).thenReturn(false);
        when(rolRepository.findById(1L)).thenReturn(Optional.empty());

        // When / Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> usuarioService.registrarUsuario(request));

        assertEquals("Rol con ID 1 no encontrado", exception.getMessage());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void actualizarRoles_cuandoUsuarioYRolExisten_debeReemplazarRoles() {
        // Given
        Usuario usuarioExistente = Usuario.builder()
                .idUsuario(5L)
                .nombre("Gabriel Martinez")
                .email("gabriel@vetnova.cl")
                .estado("ACTIVE")
                .idSucursal(10L)
                .rolesAsignados(new ArrayList<>())
                .build();

        Rol nuevoRol = Rol.builder()
                .idRol(2L)
                .nombreRol("VETERINARIO")
                .build();

        when(usuarioRepository.findById(5L)).thenReturn(Optional.of(usuarioExistente));
        when(rolRepository.findById(2L)).thenReturn(Optional.of(nuevoRol));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        UsuarioResponseDTO respuesta = usuarioService.actualizarRoles(5L, Set.of(2L));

        // Then
        assertEquals(5L, respuesta.getIdUsuario());
        assertEquals(1, respuesta.getRoles().size());
        assertTrue(respuesta.getRoles().contains("VETERINARIO"));
        verify(usuarioRepository).save(usuarioExistente);
    }

    @Test
    void desactivarUsuarioLogico_cuandoUsuarioExiste_debeCambiarEstadoInactive() {
        // Given
        Usuario usuario = Usuario.builder()
                .idUsuario(8L)
                .nombre("Usuario Activo")
                .email("activo@vetnova.cl")
                .estado("ACTIVE")
                .idSucursal(1L)
                .rolesAsignados(new ArrayList<>())
                .build();

        when(usuarioRepository.findById(8L)).thenReturn(Optional.of(usuario));

        // When
        usuarioService.desactivarUsuarioLogico(8L);

        // Then
        assertEquals("INACTIVE", usuario.getEstado());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void desactivarUsuarioLogico_cuandoUsuarioNoExiste_debeLanzarResourceNotFoundException() {
        // Given
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> usuarioService.desactivarUsuarioLogico(99L));

        assertEquals("Usuario no encontrado", exception.getMessage());
        verify(usuarioRepository, never()).save(any());
    }
}
