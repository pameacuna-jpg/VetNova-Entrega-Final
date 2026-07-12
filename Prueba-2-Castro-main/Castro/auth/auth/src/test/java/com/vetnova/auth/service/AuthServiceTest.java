package com.vetnova.auth.service;

import com.vetnova.auth.dto.LoginRequest;
import com.vetnova.auth.dto.RegistroUsuarioRequest;
import com.vetnova.auth.exception.InvalidCredentialsException;
import com.vetnova.auth.model.AuthUsuario;
import com.vetnova.auth.repository.AuthUsuarioRepository;
import com.vetnova.auth.security.JwtUtil;
import com.vetnova.auth.event.EventoDominio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private AuthService authService;

    private LoginRequest request;
    private AuthUsuario usuario;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "notificacionesUrl", "http://localhost:8089/api/v1/notificaciones");

        // Preparamos los datos de entrada
        request = new LoginRequest();
        request.setEmail("admin@vetnova.cl");
        request.setPassword("123456");

        // Preparamos el usuario simulado de la BD
        usuario = new AuthUsuario();
        usuario.setEmail("admin@vetnova.cl");
        usuario.setPassword("hashed_password_desde_bd");
        usuario.setRol("ADMIN");
        usuario.setActivo(true);
    }

    @Test
    void procesarLogin_UsuarioInactivo_DebeLanzarExcepcion() {
        usuario.setActivo(false);
        when(repository.findByEmail(request.getEmail())).thenReturn(Optional.of(usuario));

        assertThrows(InvalidCredentialsException.class, () -> authService.procesarLogin(request));

        verify(eventPublisher, times(1)).publishEvent(any(EventoDominio.class));
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtUtil, never()).generateToken(anyString());
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

    @SuppressWarnings("unchecked")
    @Test
    void procesarLogin_Exito_DebeEnviarNotificacionInternaConPayloadCorrecto() {
        when(repository.findByEmail(request.getEmail())).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(request.getPassword(), usuario.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(usuario.getEmail())).thenReturn("token-jwt-generado");

        authService.procesarLogin(request);

        ArgumentCaptor<Map> captor = ArgumentCaptor.forClass(Map.class);
        verify(restTemplate).postForEntity(eq("http://localhost:8089/api/v1/notificaciones"), captor.capture(), eq(Object.class));

        Map<String, Object> payload = captor.getValue();
        assertEquals("INTERNA", payload.get("destino"));
        assertNull(payload.get("idCliente"));
        assertEquals(usuario.getEmail(), payload.get("destinatario"));
        assertEquals("SISTEMA", payload.get("tipo"));
        assertEquals("EMAIL", payload.get("canal"));
        assertEquals("MEDIA", payload.get("prioridad"));
    }

    @SuppressWarnings("unchecked")
    @Test
    void procesarLogin_Fallido_DebeEnviarNotificacionInternaConPrioridadAlta() {
        when(repository.findByEmail(request.getEmail())).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(request.getPassword(), usuario.getPassword())).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authService.procesarLogin(request));

        ArgumentCaptor<Map> captor = ArgumentCaptor.forClass(Map.class);
        verify(restTemplate).postForEntity(eq("http://localhost:8089/api/v1/notificaciones"), captor.capture(), eq(Object.class));

        Map<String, Object> payload = captor.getValue();
        assertEquals("INTERNA", payload.get("destino"));
        assertEquals(usuario.getEmail(), payload.get("destinatario"));
        assertEquals("ALTA", payload.get("prioridad"));
    }

    @Test
    void procesarLogin_siNotificacionesFalla_noDebeInterrumpirElLoginExitoso() {
        when(repository.findByEmail(request.getEmail())).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(request.getPassword(), usuario.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(usuario.getEmail())).thenReturn("token-jwt-generado");
        when(restTemplate.postForEntity(anyString(), any(), eq(Object.class)))
                .thenThrow(new ResourceAccessException("down"));

        String token = assertDoesNotThrow(() -> authService.procesarLogin(request));

        assertEquals("token-jwt-generado", token);
    }

    @Test
    void registrar_debeGuardarUsuarioYEnviarNotificacionInterna() {
        when(passwordEncoder.encode("123456")).thenReturn("hash-123456");
        when(jwtUtil.generateToken("nuevo@vetnova.cl")).thenReturn("token-nuevo-usuario");

        RegistroUsuarioRequest registro = new RegistroUsuarioRequest();
        registro.setEmail("nuevo@vetnova.cl");
        registro.setPassword("123456");
        registro.setRol("RECEPCION");

        var response = authService.registrar(registro);

        assertNotNull(response);
        assertEquals("token-nuevo-usuario", response.getToken());
        verify(repository).save(any(AuthUsuario.class));
        verify(restTemplate).postForEntity(eq("http://localhost:8089/api/v1/notificaciones"), any(), eq(Object.class));
    }

    @Test
    void registrar_siNotificacionesFalla_noDebeInterrumpirElRegistro() {
        when(passwordEncoder.encode("123456")).thenReturn("hash-123456");
        when(jwtUtil.generateToken("nuevo@vetnova.cl")).thenReturn("token-nuevo-usuario");
        when(restTemplate.postForEntity(anyString(), any(), eq(Object.class)))
                .thenThrow(new ResourceAccessException("down"));

        RegistroUsuarioRequest registro = new RegistroUsuarioRequest();
        registro.setEmail("nuevo@vetnova.cl");
        registro.setPassword("123456");
        registro.setRol("RECEPCION");

        var response = assertDoesNotThrow(() -> authService.registrar(registro));

        assertEquals("token-nuevo-usuario", response.getToken());
    }
}