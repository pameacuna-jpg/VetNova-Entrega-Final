package com.vetnova.notificaciones.service;

import com.vetnova.notificaciones.client.ClienteClient;
import com.vetnova.notificaciones.dto.ClienteResponseDTO;
import com.vetnova.notificaciones.dto.NotificacionRequestDTO;
import com.vetnova.notificaciones.dto.NotificacionResponseDTO;
import com.vetnova.notificaciones.enums.EstadoNotificacion;
import com.vetnova.notificaciones.enums.TipoNotificacion;
import com.vetnova.notificaciones.exception.NegocioException;
import com.vetnova.notificaciones.exception.RecursoNoEncontradoException;
import com.vetnova.notificaciones.model.Notificacion;
import com.vetnova.notificaciones.repository.NotificacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;
    private final ClienteClient clienteClient;

    public NotificacionResponseDTO crear(NotificacionRequestDTO request) {
        ClienteResponseDTO cliente = clienteClient.obtenerClientePorId(request.getIdCliente());

        validarCliente(cliente);

        String destinatario = obtenerDestinatarioSegunCanal(request, cliente);

        Notificacion notificacion = Notificacion.builder()
                .idCliente(cliente.getIdCliente())
                .destinatario(destinatario)
                .mensaje(request.getMensaje().trim())
                .tipo(request.getTipo())
                .canal(request.getCanal())
                .prioridad(request.getPrioridad())
                .estado(EstadoNotificacion.PENDIENTE)
                .build();

        Notificacion guardada = notificacionRepository.save(notificacion);

        return convertirAResponse(guardada);
    }

    public List<NotificacionResponseDTO> listar() {
        return notificacionRepository.findAll()
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    public NotificacionResponseDTO buscarPorId(Long id) {
        Notificacion notificacion = obtenerEntidadPorId(id);
        return convertirAResponse(notificacion);
    }

    public List<NotificacionResponseDTO> buscarPorCliente(Long idCliente) {
        clienteClient.obtenerClientePorId(idCliente);

        return notificacionRepository.findByIdCliente(idCliente)
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    public List<NotificacionResponseDTO> buscarPorEstado(EstadoNotificacion estado) {
        return notificacionRepository.findByEstado(estado)
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    public List<NotificacionResponseDTO> buscarPorTipo(TipoNotificacion tipo) {
        return notificacionRepository.findByTipo(tipo)
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    public NotificacionResponseDTO marcarComoEnviada(Long id) {
        Notificacion notificacion = obtenerEntidadPorId(id);

        if (notificacion.getEstado() == EstadoNotificacion.ENVIADA) {
            throw new NegocioException("La notificación ya se encuentra marcada como ENVIADA");
        }

        notificacion.setEstado(EstadoNotificacion.ENVIADA);

        return convertirAResponse(notificacionRepository.save(notificacion));
    }

    public NotificacionResponseDTO marcarComoError(Long id) {
        Notificacion notificacion = obtenerEntidadPorId(id);

        if (notificacion.getEstado() == EstadoNotificacion.ERROR) {
            throw new NegocioException("La notificación ya se encuentra marcada como ERROR");
        }

        notificacion.setEstado(EstadoNotificacion.ERROR);

        return convertirAResponse(notificacionRepository.save(notificacion));
    }

    public void eliminar(Long id) {
        Notificacion notificacion = obtenerEntidadPorId(id);
        notificacionRepository.delete(notificacion);
    }

    private Notificacion obtenerEntidadPorId(Long id) {
        return notificacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe una notificación con id " + id
                ));
    }

    private void validarCliente(ClienteResponseDTO cliente) {
        if (cliente == null || cliente.getIdCliente() == null) {
            throw new NegocioException("No fue posible obtener los datos del cliente");
        }

        if (cliente.getEstado() != null && !cliente.getEstado().equalsIgnoreCase("ACTIVO")) {
            throw new NegocioException("El cliente no se encuentra activo");
        }
    }

    private String obtenerDestinatarioSegunCanal(NotificacionRequestDTO request, ClienteResponseDTO cliente) {
        return switch (request.getCanal()) {
            case EMAIL -> {
                if (cliente.getEmail() == null || cliente.getEmail().isBlank()) {
                    throw new NegocioException("El cliente no tiene email registrado");
                }
                yield cliente.getEmail();
            }
            case SMS, WHATSAPP -> {
                if (cliente.getTelefono() == null || cliente.getTelefono().isBlank()) {
                    throw new NegocioException("El cliente no tiene teléfono registrado");
                }
                yield cliente.getTelefono();
            }
        };
    }

    private NotificacionResponseDTO convertirAResponse(Notificacion notificacion) {
        return NotificacionResponseDTO.builder()
                .idNotificacion(notificacion.getIdNotificacion())
                .idCliente(notificacion.getIdCliente())
                .destinatario(notificacion.getDestinatario())
                .mensaje(notificacion.getMensaje())
                .tipo(notificacion.getTipo())
                .estado(notificacion.getEstado())
                .canal(notificacion.getCanal())
                .prioridad(notificacion.getPrioridad())
                .fechaCreacion(notificacion.getFechaCreacion())
                .build();
    }
}