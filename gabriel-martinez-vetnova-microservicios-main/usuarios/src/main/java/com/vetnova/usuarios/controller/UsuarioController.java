package com.vetnova.usuarios.controller;

import com.vetnova.usuarios.dto.*;
import com.vetnova.usuarios.service.IUsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final IUsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> registrarUsuario(@Valid @RequestBody UsuarioRequestDTO dto) {
        UsuarioResponseDTO response = usuarioService.registrarUsuario(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarUsuarios() {
        List<UsuarioResponseDTO> response = usuarioService.listarTodosLosUsuarios();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/roles")
    public ResponseEntity<UsuarioResponseDTO> actualizarRoles(
            @PathVariable Long id, 
            @RequestBody Set<Long> idsRoles) {
        return ResponseEntity.ok(usuarioService.actualizarRoles(id, idsRoles));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivarUsuario(@PathVariable Long id) {
        usuarioService.desactivarUsuarioLogico(id);
        return ResponseEntity.noContent().build(); // 204 No Content bajo buenas prácticas REST
    }
}