package com.vetnova.auth.controller;

import com.vetnova.auth.dto.AuthResponse;
import com.vetnova.auth.dto.LoginRequest;
import com.vetnova.auth.dto.RegistroUsuarioRequest;
import com.vetnova.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/registro")
    public ResponseEntity<AuthResponse> registrar(@Valid @RequestBody RegistroUsuarioRequest request) {
        // Llamamos al método de registro que agregaremos en el Paso 2
        AuthResponse response = authService.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody LoginRequest request) {
        // 1. Llamamos al método correcto que programamos hoy (procesarLogin)
        String token = authService.procesarLogin(request);

        // 2. Mapeamos la respuesta tal como lo espera tu AuthControllerTest
        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Autenticación exitosa");
        response.put("token", token);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/recuperar-password")
    public ResponseEntity<Map<String, String>> recuperarPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String mensaje = authService.recuperarContrasena(email);

        Map<String, String> response = new HashMap<>();
        response.put("mensaje", mensaje);
        response.put("status", "success");

        return ResponseEntity.ok(response);
    }
}