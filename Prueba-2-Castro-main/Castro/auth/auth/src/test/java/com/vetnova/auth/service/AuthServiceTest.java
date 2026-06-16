package com.vetnova.auth.service;

import com.vetnova.auth.dto.AuthResponse;
import com.vetnova.auth.dto.LoginRequest;
import com.vetnova.auth.dto.RegistroUsuarioRequest;
import com.vetnova.auth.model.AuthUsuario;
import com.vetnova.auth.repository.AuthUsuarioRepository;
import com.vetnova.auth.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthUsuarioRepository authUsuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private RegistroUsuarioRequest registroRequest;
    private LoginRequest loginRequest;
    private AuthUsuario usuario;

    @BeforeEach
    void setUp() {
        registroRequest = new RegistroUsuarioRequest();
        registroRequest.setEmail("admin@vetnova.cl");
        registroRequest.setPassword("123456");
        registroRequest.setRol("ADMIN");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("admin@vetnova.cl");
        loginRequest.setPassword("123456");

        usuario = AuthUsuario.builder()
                .id(1L)
                .email("admin@vetnova.cl")
                .password("password-encriptada")
                .rol("ADMIN")
                .activo(true)
                .build();
    }

    @Test
    void registrar_deberiaCrearUsuarioYRetornarToken() {
        // Given
        when(authUsuarioRepository.existsByEmail("admin@vetnova.cl")).thenReturn(false);
        when(passwordEncoder.encode("123456")).thenReturn("password-encriptada");
        when(authUsuarioRepository.save(any(AuthUsuario.class))).thenReturn(usuario);
        when(jwtUtil.generarToken("admin@vetnova.cl", "ADMIN")).thenReturn("token-test");

        // When
        AuthResponse response = authService.registrar(registroRequest);

        // Then
        assertNotNull(response);
        assertEquals("token-test", response.getToken());
        assertEquals("admin@vetnova.cl", response.getEmail());
        assertEquals("ADMIN", response.getRol());

        verify(authUsuarioRepository).existsByEmail("admin@vetnova.cl");
        verify(passwordEncoder).encode("123456");
        verify(authUsuarioRepository).save(any(AuthUsuario.class));
        verify(jwtUtil).generarToken("admin@vetnova.cl", "ADMIN");
    }

    @Test
    void registrar_deberiaLanzarExcepcionSiEmailYaExiste() {
        // Given
        when(authUsuarioRepository.existsByEmail("admin@vetnova.cl")).thenReturn(true);

        // When
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> authService.registrar(registroRequest)
        );

        // Then
        assertEquals("El email ya está registrado", exception.getMessage());
        verify(authUsuarioRepository).existsByEmail("admin@vetnova.cl");
        verify(authUsuarioRepository, never()).save(any(AuthUsuario.class));
    }

    @Test
    void login_deberiaRetornarTokenCuandoCredencialesSonValidas() {
        // Given
        when(authUsuarioRepository.findByEmail("admin@vetnova.cl")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("123456", "password-encriptada")).thenReturn(true);
        when(jwtUtil.generarToken("admin@vetnova.cl", "ADMIN")).thenReturn("token-login");

        // When
        AuthResponse response = authService.login(loginRequest);

        // Then
        assertNotNull(response);
        assertEquals("token-login", response.getToken());
        assertEquals("admin@vetnova.cl", response.getEmail());
        assertEquals("ADMIN", response.getRol());

        verify(authUsuarioRepository).findByEmail("admin@vetnova.cl");
        verify(passwordEncoder).matches("123456", "password-encriptada");
        verify(jwtUtil).generarToken("admin@vetnova.cl", "ADMIN");
    }

    @Test
    void login_deberiaLanzarExcepcionSiUsuarioNoExiste() {
        // Given
        when(authUsuarioRepository.findByEmail("admin@vetnova.cl")).thenReturn(Optional.empty());

        // When
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> authService.login(loginRequest)
        );

        // Then
        assertEquals("Credenciales inválidas", exception.getMessage());
        verify(authUsuarioRepository).findByEmail("admin@vetnova.cl");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void login_deberiaLanzarExcepcionSiUsuarioEstaInactivo() {
        // Given
        usuario.setActivo(false);
        when(authUsuarioRepository.findByEmail("admin@vetnova.cl")).thenReturn(Optional.of(usuario));

        // When
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> authService.login(loginRequest)
        );

        // Then
        assertEquals("Usuario inactivo", exception.getMessage());
        verify(authUsuarioRepository).findByEmail("admin@vetnova.cl");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void login_deberiaLanzarExcepcionSiPasswordEsIncorrecta() {
        // Given
        when(authUsuarioRepository.findByEmail("admin@vetnova.cl")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("123456", "password-encriptada")).thenReturn(false);

        // When
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> authService.login(loginRequest)
        );

        // Then
        assertEquals("Credenciales inválidas", exception.getMessage());
        verify(authUsuarioRepository).findByEmail("admin@vetnova.cl");
        verify(passwordEncoder).matches("123456", "password-encriptada");
        verify(jwtUtil, never()).generarToken(anyString(), anyString());
    }
}