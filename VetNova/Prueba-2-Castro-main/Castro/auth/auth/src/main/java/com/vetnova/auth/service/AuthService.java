package com.vetnova.auth.service;

import com.vetnova.auth.dto.AuthResponse;
import com.vetnova.auth.dto.LoginRequest;
import com.vetnova.auth.dto.RegistroUsuarioRequest;
import com.vetnova.auth.model.AuthUsuario;
import com.vetnova.auth.repository.AuthUsuarioRepository;
import com.vetnova.auth.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthUsuarioRepository authUsuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(AuthUsuarioRepository authUsuarioRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.authUsuarioRepository = authUsuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse registrar(RegistroUsuarioRequest request) {

        if (authUsuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        AuthUsuario usuario = AuthUsuario.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .rol(request.getRol())
                .activo(true)
                .build();

        AuthUsuario usuarioGuardado = authUsuarioRepository.save(usuario);

        String token = jwtUtil.generarToken(usuarioGuardado.getEmail(), usuarioGuardado.getRol());

        return new AuthResponse(
                token,
                usuarioGuardado.getEmail(),
                usuarioGuardado.getRol()
        );
    }

    public AuthResponse login(LoginRequest request) {

        AuthUsuario usuario = authUsuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));

        if (!usuario.getActivo()) {
            throw new RuntimeException("Usuario inactivo");
        }

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new RuntimeException("Credenciales inválidas");
        }

        String token = jwtUtil.generarToken(usuario.getEmail(), usuario.getRol());

        return new AuthResponse(
                token,
                usuario.getEmail(),
                usuario.getRol()
        );
    }
}
