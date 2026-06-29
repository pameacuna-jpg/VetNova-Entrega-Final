package com.vetnova.auth.service;

import com.vetnova.auth.dto.LoginRequest;
import com.vetnova.auth.exception.InvalidCredentialsException;
import com.vetnova.auth.model.AuthUsuario;
import com.vetnova.auth.repository.AuthUsuarioRepository;
import com.vetnova.auth.security.JwtUtil;
import com.vetnova.auth.event.EventoDominio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthUsuarioRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private AuthService authService;

    private LoginRequest request;
    private AuthUsuario usuario;

    @BeforeEach
    void setUp() {
        // Preparamos los datos de entrada
        request = new LoginRequest();
        request.setEmail("admin@vetnova.cl");
        request.setPassword("123456");

        // Preparamos el usuario simulado de la BD
        usuario = new AuthUsuario();
        usuario.setEmail("admin@vetnova.cl");
        usuario.setPassword("hashed_password_desde_bd");
        usuario.setRol("ADMIN");
    }

    @Test
    void procesarLogin_Exito_DebeRetornarTokenYPublicarEvento() {
        // Given: Simulamos que el usuario existe y la contraseña coincide
        when(repository.findByEmail(request.getEmail())).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(request.getPassword(), usuario.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(usuario.getEmail())).thenReturn("token-jwt-generado");

        // When: Ejecutamos el login
        String token = authService.procesarLogin(request);

        // Then: Verificamos el resultado y que se publicó el evento LoginExitoso
        assertEquals("token-jwt-generado", token);
        verify(eventPublisher, times(1)).publishEvent(any(EventoDominio.class));
    }

    @Test
    void procesarLogin_UsuarioNoEncontrado_DebeLanzarExcepcion() {
        // Given: Simulamos que la BD no encuentra al usuario
        when(repository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        // When & Then: Esperamos que arroje el error y publique el evento LoginFallido
        assertThrows(InvalidCredentialsException.class, () -> authService.procesarLogin(request));
        
        verify(eventPublisher, times(1)).publishEvent(any(EventoDominio.class));
        verify(passwordEncoder, never()).matches(anyString(), anyString()); // Nunca debe llegar a comparar contraseñas
    }

    @Test
    void procesarLogin_ContrasenaIncorrecta_DebeLanzarExcepcion() {
        // Given: Simulamos que el usuario existe pero la contraseña falla
        when(repository.findByEmail(request.getEmail())).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(request.getPassword(), usuario.getPassword())).thenReturn(false);

        // When & Then: Esperamos el error y el evento LoginFallido
        assertThrows(InvalidCredentialsException.class, () -> authService.procesarLogin(request));

        verify(eventPublisher, times(1)).publishEvent(any(EventoDominio.class));
        verify(jwtUtil, never()).generateToken(anyString()); // Nunca debe generar un token
    }

    @Test
    void recuperarContrasena_DebeRetornarMensaje() {
        String resultado = authService.recuperarContrasena("test@test.cl");
        assertEquals("Se han enviado las instrucciones a test@test.cl", resultado);
    }
}