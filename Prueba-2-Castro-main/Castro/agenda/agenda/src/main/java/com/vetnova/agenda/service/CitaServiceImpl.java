package com.vetnova.agenda.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.vetnova.agenda.dto.CitaRequestDTO;
import com.vetnova.agenda.dto.CitaResponseDTO;
import com.vetnova.agenda.event.CitaAgendadaEvent;
import com.vetnova.agenda.exception.ResourceNotFoundException;
import com.vetnova.agenda.exception.ServiceUnavailableException;
import com.vetnova.agenda.model.Cita;
import com.vetnova.agenda.repository.CitaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CitaServiceImpl implements CitaService {

    private final CitaRepository citaRepository;
    private final RestTemplate restTemplate;
    private final ApplicationEventPublisher eventPublisher;

    @Value("${services.clientes.url}")
    private String clientesServiceUrl;

    @Value("${services.mascotas.url}")
    private String mascotasServiceUrl;

    @Override
    public CitaResponseDTO agendarHora(CitaRequestDTO requestDTO) {
        log.info("Iniciando validación externa para idCliente: {} y idMascota: {}", requestDTO.getIdCliente(), requestDTO.getIdMascota());

        validarRecursoExistente(clientesServiceUrl, "/api/v1/clientes/", requestDTO.getIdCliente(), "cliente");
        validarRecursoExistente(mascotasServiceUrl, "/api/v1/mascotas/", requestDTO.getIdMascota(), "mascota");

        Cita cita = new Cita();
        cita.setIdCliente(requestDTO.getIdCliente());
        cita.setIdMascota(requestDTO.getIdMascota());
        cita.setIdVeterinario(requestDTO.getIdVeterinario());
        cita.setFechaHora(requestDTO.getFechaHora());
        cita.setMotivo(requestDTO.getMotivo());
        cita.setEstado("AGENDADA");

        Cita citaGuardada = citaRepository.save(cita);

        eventPublisher.publishEvent(new CitaAgendadaEvent(
                this, // <-- ¡Faltaba esto! Es obligatorio en Spring Events
                citaGuardada.getId(),
                citaGuardada.getIdCliente(),
                citaGuardada.getIdMascota(),
                citaGuardada.getFechaHora()
        ));

        log.info("Evento CitaAgendada emitido para la cita ID: {}", citaGuardada.getId());
        return mapToResponse(citaGuardada);
    }

    @Override
    public CitaResponseDTO reprogramarHora(Long id, LocalDateTime nuevaFecha) {
        Cita cita = obtenerCitaEntidad(id);
        cita.setFechaHora(nuevaFecha);
        cita.setEstado("REPROGRAMADA");
        return mapToResponse(citaRepository.save(cita));
    }

    @Override
    public CitaResponseDTO cancelarHora(Long id) {
        Cita cita = obtenerCitaEntidad(id);
        cita.setEstado("CANCELADA");
        return mapToResponse(citaRepository.save(cita));
    }

    @Override
    public CitaResponseDTO confirmarAsistencia(Long id) {
        Cita cita = obtenerCitaEntidad(id);
        cita.setEstado("CONFIRMADA");
        return mapToResponse(citaRepository.save(cita));
    }

    @Override
    public List<CitaResponseDTO> obtenerTodasLasCitas() {
        return citaRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public CitaResponseDTO obtenerCitaPorId(Long id) {
        return mapToResponse(obtenerCitaEntidad(id));
    }

    @Override
    public List<CitaResponseDTO> obtenerCitasProximas24h() {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime manana = ahora.plusDays(1);

        return citaRepository.findAll().stream()
                .filter(cita -> cita.getFechaHora().isAfter(ahora)
                        && cita.getFechaHora().isBefore(manana))
                .map(this::mapToResponse)
                .toList();
    }

    private void validarRecursoExistente(String baseUrl, String path, Long recursoId, String nombreRecurso) {
        String url = baseUrl + path + recursoId;

        try {
            restTemplate.getForEntity(url, Object.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ResourceNotFoundException("El " + nombreRecurso + " con ID " + recursoId + " no existe.");
            }
            throw new ResourceNotFoundException("No se pudo validar el " + nombreRecurso + " con ID " + recursoId + ".");
        } catch (ResourceAccessException e) {
            throw new ServiceUnavailableException("El microservicio de " + capitalizar(nombreRecurso) + " no se encuentra activo. Operación abortada por integridad.");
        }
    }

    private String capitalizar(String texto) {
        if (texto == null || texto.isBlank()) {
            return texto;
        }
        return Character.toUpperCase(texto.charAt(0)) + texto.substring(1);
    }

    private Cita obtenerCitaEntidad(Long id) {
        return citaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada con ID: " + id));
    }

    private CitaResponseDTO mapToResponse(Cita cita) {
        return new CitaResponseDTO(
                cita.getId(),
                cita.getIdCliente(),
                cita.getIdMascota(),
                cita.getIdVeterinario(),
                cita.getFechaHora(),
                cita.getMotivo(),
                cita.getEstado()
        );
    }
}