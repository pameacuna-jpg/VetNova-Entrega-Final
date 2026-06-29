package com.vetnova.auth.service;

import com.vetnova.auth.dto.LoginRequest;
import com.vetnova.auth.exception.InvalidCredentialsException;
import com.vetnova.auth.model.AuthUsuario;
import com.vetnova.auth.repository.AuthUsuarioRepository;
import com.vetnova.auth.security.JwtUtil;
import com.vetnova.auth.event.EventoDominio;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class AuthService {

    private final AuthUsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final ApplicationEventPublisher eventPublisher;

    public AuthService(AuthUsuarioRepository repository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.eventPublisher = eventPublisher;
    }

    public String procesarLogin(LoginRequest request) {
        log.info("Iniciando proceso de login real en BD para: {}", request.getEmail());

        // 1. Buscar usuario en la base de datos real
        AuthUsuario usuario = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    emitirEventoLoginFallido(request.getEmail(), "Usuario no encontrado en BD");
                    return new InvalidCredentialsException("Credenciales inválidas");
                });

        // 2. Comparar la contraseña ingresada vs el Hash BCrypt en la base de datos
        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            emitirEventoLoginFallido(request.getEmail(), "Contraseña incorrecta");
            throw new InvalidCredentialsException("Credenciales inválidas");
        }

        // 3. Generar el Token JWT
        String token = jwtUtil.generateToken(usuario.getEmail());

        // 4. Emitir el evento asíncrono obligatorio de éxito
        Map<String, Object> payload = new HashMap<>();
        payload.put("email", usuario.getEmail());
        payload.put("rol", usuario.getRol());

        EventoDominio<Map<String, Object>> eventoExito = new EventoDominio<>(
                "LoginExitoso",
                "ms-auth",
                payload
        );
        eventPublisher.publishEvent(eventoExito);

        log.info("Autenticación exitosa. Token generado para: {}", usuario.getEmail());
        return token;
    }

    private void emitirEventoLoginFallido(String email, String razon) {
        log.warn("Login fallido para {}: {}", email, razon);
        Map<String, Object> payload = new HashMap<>();
        payload.put("email", email);
        payload.put("razon", razon);

        EventoDominio<Map<String, Object>> eventoFallo = new EventoDominio<>(
                "LoginFallido",
                "ms-auth",
                payload
        );
        eventPublisher.publishEvent(eventoFallo);
    }

    public String recuperarContrasena(String email) {
        return "Se han enviado las instrucciones a " + email;
    }
    
// Método agregado para registrar usuarios directamente en la BD de Auth
    public com.vetnova.auth.dto.AuthResponse registrar(com.vetnova.auth.dto.RegistroUsuarioRequest request) {
        log.info("Registrando nuevas credenciales en la BD para: {}", request.getEmail());

        AuthUsuario nuevoUsuario = new AuthUsuario();
        nuevoUsuario.setEmail(request.getEmail());
        // Encriptamos la contraseña para cumplir con el estándar de seguridad
        nuevoUsuario.setPassword(passwordEncoder.encode(request.getPassword()));
        nuevoUsuario.setRol(request.getRol());
        nuevoUsuario.setActivo(true);
        nuevoUsuario.setFechaCreacion(java.time.LocalDateTime.now());

        repository.save(nuevoUsuario);

        // Retornamos el DTO como espera el controlador
        com.vetnova.auth.dto.AuthResponse response = new com.vetnova.auth.dto.AuthResponse();
        response.setToken(jwtUtil.generateToken(nuevoUsuario.getEmail()));
        return response;
    }
}