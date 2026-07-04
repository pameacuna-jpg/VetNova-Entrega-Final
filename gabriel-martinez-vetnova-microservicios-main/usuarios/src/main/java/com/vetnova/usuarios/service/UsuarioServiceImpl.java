package com.vetnova.usuarios.service;

import com.vetnova.usuarios.dto.UsuarioRequestDTO;
import com.vetnova.usuarios.dto.UsuarioResponseDTO;
import com.vetnova.usuarios.exception.ResourceNotFoundException;
import com.vetnova.usuarios.model.Rol;
import com.vetnova.usuarios.model.Usuario;
import com.vetnova.usuarios.model.UsuarioRol;
import com.vetnova.usuarios.repository.RolRepository;
import com.vetnova.usuarios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements IUsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;

    @Override
    @Transactional
    public UsuarioResponseDTO registrarUsuario(UsuarioRequestDTO request) {
        log.info("HU-UR01: Iniciando registro de usuario con email: {}", request.getEmail());

        if (usuarioRepository.existsByEmail(request.getEmail())) {
            log.warn("Intento de registro con email ya existente: {}", request.getEmail());
            throw new IllegalArgumentException("El email ya se encuentra registrado");
        }

        Usuario usuario = Usuario.builder()
                .nombre(request.getNombre())
                .email(request.getEmail())
                .password("$2a$10$SimulatedBcryptHash...")
                .estado("PENDIENTE_ACTIVACION")
                .idSucursal(request.getIdSucursal())
                .rolesAsignados(new ArrayList<>())
                .build();

        List<UsuarioRol> rolesAsignados = request.getIdsRoles().stream().map(idRol -> {
            Rol rol = rolRepository.findById(idRol)
                    .orElseThrow(() -> new ResourceNotFoundException("Rol con ID " + idRol + " no encontrado"));

            return UsuarioRol.builder()
                    .usuario(usuario)
                    .rol(rol)
                    .fechaAsignacion(LocalDateTime.now())
                    .build();
        }).collect(Collectors.toList());

        usuario.getRolesAsignados().addAll(rolesAsignados);

        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        log.info("Usuario creado exitosamente con ID: {}", usuarioGuardado.getIdUsuario());

        return mapToResponseDTO(usuarioGuardado);
    }

    @Override
    @Transactional
    public UsuarioResponseDTO actualizarRoles(Long idUsuario, Set<Long> idsRoles) {
        log.info("HU-UR02: Modificando roles para usuario ID: {}", idUsuario);

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        usuario.getRolesAsignados().clear();

        List<UsuarioRol> nuevosRoles = idsRoles.stream().map(idRol -> {
            Rol rol = rolRepository.findById(idRol)
                    .orElseThrow(() -> new ResourceNotFoundException("Rol con ID " + idRol + " no encontrado"));

            return UsuarioRol.builder()
                    .usuario(usuario)
                    .rol(rol)
                    .fechaAsignacion(LocalDateTime.now())
                    .build();
        }).collect(Collectors.toList());

        usuario.getRolesAsignados().addAll(nuevosRoles);

        Usuario usuarioActualizado = usuarioRepository.save(usuario);

        return mapToResponseDTO(usuarioActualizado);
    }

    @Override
    @Transactional
    public void desactivarUsuarioLogico(Long idUsuario) {
        log.info("HU-UR05: Solicitud de baja lógica para usuario ID: {}", idUsuario);

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        usuario.setEstado("INACTIVE");

        usuarioRepository.save(usuario);

        log.info("Usuario ID: {} cambiado a estado INACTIVE correctamente.", idUsuario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarTodosLosUsuarios() {
        log.info("HU-UR06: Solicitando listado completo de usuarios del sistema");
        
        return usuarioRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    private UsuarioResponseDTO mapToResponseDTO(Usuario usuario) {
        List<String> nombresRoles = usuario.getRolesAsignados().stream()
                .map(usuarioRol -> usuarioRol.getRol().getNombreRol())
                .collect(Collectors.toList());

        return UsuarioResponseDTO.builder()
                .idUsuario(usuario.getIdUsuario())
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .estado(usuario.getEstado())
                .idSucursal(usuario.getIdSucursal())
                .roles(nombresRoles)
                .build();
    }
}