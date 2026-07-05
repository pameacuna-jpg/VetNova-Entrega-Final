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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

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
    private final PasswordEncoder passwordEncoder;
    private final RestTemplate restTemplate;

    @Value("${services.sucursales.url:http://localhost:8090}")
    private String sucursalesServiceUrl;

    @Override
    @Transactional
    public UsuarioResponseDTO registrarUsuario(UsuarioRequestDTO request) {
        log.info("HU-UR01: Iniciando registro de usuario con email: {}", request.getEmail());

        if (usuarioRepository.existsByEmail(request.getEmail())) {
            log.warn("Intento de registro con email ya existente: {}", request.getEmail());
            throw new IllegalArgumentException("El email ya se encuentra registrado");
        }

        validarSucursalExistente(request.getIdSucursal());

        Usuario usuario = Usuario.builder()
                .nombre(request.getNombre())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
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

    private void validarSucursalExistente(Long idSucursal) {
        String url = sucursalesServiceUrl + "/api/v1/sucursales/" + idSucursal;
        try {
            restTemplate.getForEntity(url, Object.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ResourceNotFoundException("La sucursal con ID " + idSucursal + " no existe.");
            }
            throw new ResourceNotFoundException("No se pudo validar la sucursal con ID " + idSucursal + ".");
        } catch (ResourceAccessException e) {
            throw new com.vetnova.usuarios.exception.ServiceUnavailableException("El microservicio de Sucursales no se encuentra activo. Operación abortada por integridad.");
        }
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