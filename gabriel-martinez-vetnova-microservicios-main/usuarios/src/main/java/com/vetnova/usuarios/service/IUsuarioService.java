package com.vetnova.usuarios.service;

import com.vetnova.usuarios.dto.UsuarioRequestDTO;
import com.vetnova.usuarios.dto.UsuarioResponseDTO;
import java.util.List;
import java.util.Set;

public interface IUsuarioService {

    UsuarioResponseDTO registrarUsuario(UsuarioRequestDTO request);

    UsuarioResponseDTO actualizarRoles(Long idUsuario, Set<Long> idsRoles);

    void desactivarUsuarioLogico(Long idUsuario);

    // Método agregado para listar todos los usuarios
    List<UsuarioResponseDTO> listarTodosLosUsuarios();
}