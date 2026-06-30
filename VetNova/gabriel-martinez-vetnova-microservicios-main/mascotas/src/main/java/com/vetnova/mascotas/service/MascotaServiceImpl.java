package com.vetnova.mascotas.service;

import com.vetnova.mascotas.dto.ClienteDTO;
import com.vetnova.mascotas.dto.MascotaRequestDTO;
import com.vetnova.mascotas.dto.MascotaResponseDTO;
import com.vetnova.mascotas.dto.TransferenciaRequestDTO;
import com.vetnova.mascotas.exception.ResourceNotFoundException;
import com.vetnova.mascotas.model.Especie;
import com.vetnova.mascotas.model.HistorialMascota;
import com.vetnova.mascotas.model.Mascota;
import com.vetnova.mascotas.repository.EspecieRepository;
import com.vetnova.mascotas.repository.MascotaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.Period;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MascotaServiceImpl implements IMascotaService {

    private final MascotaRepository mascotaRepository;
    private final EspecieRepository especieRepository;   
    private final RestTemplate restTemplate;

    @Value("${notificaciones.url}")
    private String notificacionesUrl;

    @Value("${clientes.url}")
    private String clientesUrl;

    @Override
    public MascotaResponseDTO registrar(MascotaRequestDTO request) {
        log.info("Registrando mascota {} para cliente {}", request.getNombre(), request.getIdCliente());

        Especie especie = especieRepository.findByNombreIgnoreCase(request.getEspecieNombre())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "La Especie especificada no existe en el catálogo maestro"
                ));

        validarClienteExiste(request.getIdCliente());

        Mascota mascota = Mascota.builder()
                .nombre(request.getNombre())
                .especie(especie)
                .raza(request.getRaza())
                .sexo(request.getSexo())
                .fechaNacimiento(request.getFechaNacimiento())
                .idCliente(request.getIdCliente())
                .numeroMicrochip(request.getNumeroMicrochip())
                .estado("ACTIVO")
                .build();

        HistorialMascota historial = HistorialMascota.builder()
                .numeroHistoriaClinica(generarNumeroHistoriaClinica())
                .resumenClinico("Ficha clínica inicial creada automáticamente")
                .fechaUltimaAtencion(null)
                .ultimoPeso(Objects.requireNonNullElse(request.getUltimoPeso(), 0.0))
                .estaEsterilizado(Objects.requireNonNullElse(request.getEstaEsterilizado(), false))
                .alergiasCriticas(request.getAlergiasCriticas())
                .build();

        mascota.asignarHistorial(historial);

        Mascota mascotaGuardada = mascotaRepository.save(mascota);

        enviarNotificacionRegistroMascota(mascotaGuardada);

        return mapToResponse(mascotaGuardada);
    }

    @Override
    public MascotaResponseDTO actualizar(Long id, MascotaRequestDTO request) {
        log.info("Actualizando datos de la mascota con ID: {}", id);

        Mascota mascota = mascotaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado"));

        Especie especie = especieRepository.findByNombreIgnoreCase(request.getEspecieNombre())
                .orElseThrow(() -> new ResourceNotFoundException("La Especie especificada no existe"));

        mascota.setNombre(request.getNombre());
        mascota.setEspecie(especie);
        mascota.setRaza(request.getRaza());
        mascota.setSexo(request.getSexo());
        mascota.setFechaNacimiento(request.getFechaNacimiento());
        mascota.setNumeroMicrochip(request.getNumeroMicrochip());

        if (mascota.getHistorialMascota() != null) {
            mascota.getHistorialMascota().setUltimoPeso(
                    Objects.requireNonNullElse(request.getUltimoPeso(), 0.0)
            );
            mascota.getHistorialMascota().setEstaEsterilizado(
                    Objects.requireNonNullElse(request.getEstaEsterilizado(), false)
            );
            mascota.getHistorialMascota().setAlergiasCriticas(request.getAlergiasCriticas());
        }

        return mapToResponse(mascotaRepository.save(mascota));
    }

    @Override
    public MascotaResponseDTO obtenerPorId(Long id) {
        Mascota mascota = mascotaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado"));

        return mapToResponse(mascota);
    }

    @Override
    public Page<MascotaResponseDTO> buscarPacientes(String termino, Pageable pageable) {
        return mascotaRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Override
    public void transferirPropietario(Long id, TransferenciaRequestDTO request) {
        Mascota mascota = mascotaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado"));

        validarClienteExiste(request.getIdNuevoCliente());

        Long idClienteAnterior = mascota.getIdCliente();

        mascota.setIdCliente(request.getIdNuevoCliente());
        mascotaRepository.save(mascota);

        enviarNotificacionTransferencia(mascota, idClienteAnterior, request.getIdNuevoCliente());

        log.info("Mascota {} transferida desde cliente {} hacia cliente {}",
                id, idClienteAnterior, request.getIdNuevoCliente());
    }

    @Override
    public void actualizarEstadoVital(Long id, String nuevoEstado) {
        Mascota mascota = mascotaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado"));

        mascota.setEstado(nuevoEstado.toUpperCase());
        mascotaRepository.save(mascota);

        log.info("Estado vital actualizado para mascota {}: {}", id, nuevoEstado.concat(nuevoEstado));
    }

    private void validarClienteExiste(Long idCliente) {
        try {
            ClienteDTO cliente = restTemplate.getForObject(
                    clientesUrl + "/" + idCliente,
                    ClienteDTO.class
            );

            if (cliente == null || cliente.getIdCliente() == null) {
                throw new ResourceNotFoundException("Cliente propietario no existe");
            }

        } catch (Exception e) {
            throw new ResourceNotFoundException(
                    "Cliente propietario no existe con ID: " + idCliente
            );
        }
    }

    private void enviarNotificacionRegistroMascota(Mascota mascota) {
        try {
            Map<String, String> notificacion = Map.of(
                    "destinatario", "cliente-" + mascota.getIdCliente(),
                    "mensaje", "La mascota " + mascota.getNombre() + " fue registrada correctamente.",
                    "tipo", "MASCOTA_REGISTRADA",
                    "canal", "EMAIL",
                    "prioridad", "MEDIA"
            );

            restTemplate.postForObject(notificacionesUrl, notificacion, Void.class);

            log.info("Notificación enviada por registro de mascota ID: {}", mascota.getIdMascota());

        } catch (Exception e) {
            log.warn("No se pudo enviar notificación de registro de mascota: {}", e.getMessage());
        }
    }

    private void enviarNotificacionTransferencia(Mascota mascota, Long idClienteAnterior, Long idClienteNuevo) {
        try {
            Map<String, String> notificacion = Map.of(
                    "destinatario", "cliente-" + idClienteNuevo,
                    "mensaje", "La mascota " + mascota.getNombre()
                            + " fue transferida desde el cliente "
                            + idClienteAnterior + " hacia el cliente " + idClienteNuevo + ".",
                    "tipo", "MASCOTA_TRANSFERIDA",
                    "canal", "EMAIL",
                    "prioridad", "ALTA"
            );

            restTemplate.postForObject(notificacionesUrl, notificacion, Void.class);

            log.info("Notificación enviada por transferencia de mascota ID: {}", mascota.getIdMascota());

        } catch (Exception e) {
            log.warn("No se pudo enviar notificación de transferencia: {}", e.getMessage());
        }
    }

    private MascotaResponseDTO mapToResponse(Mascota m) {
        String edadCalculada = calcularEdad(m.getFechaNacimiento());
        HistorialMascota hm = m.getHistorialMascota();

        return MascotaResponseDTO.builder()
                .idMascota(m.getIdMascota())
                .nombre(m.getNombre())
                .especie(m.getEspecie() != null ? m.getEspecie().getNombre() : null)
                .raza(m.getRaza())
                .sexo(m.getSexo())
                .fechaNacimiento(m.getFechaNacimiento())
                .edadCalculada(edadCalculada)
                .idCliente(m.getIdCliente())
                .estado(m.getEstado())
                .numeroHistoriaClinica(hm != null ? hm.getNumeroHistoriaClinica() : null)
                .ultimoPeso(hm != null ? hm.getUltimoPeso() : null)
                .estaEsterilizado(hm != null ? hm.getEstaEsterilizado() : null)
                .alergiasCriticas(hm != null ? hm.getAlergiasCriticas() : null)
                .resumenClinico(hm != null ? hm.getResumenClinico() : null)
                .build();
    }

    private String calcularEdad(LocalDate fechaNacimiento) {
        if (fechaNacimiento == null) {
            return "Fecha no disponible";
        }

        Period periodo = Period.between(fechaNacimiento, LocalDate.now());

        if (periodo.getYears() > 0) {
            return periodo.getYears() + " años";
        }

        if (periodo.getMonths() > 0) {
            return periodo.getMonths() + " meses";
        }

        return periodo.getDays() + " días";
    }

    private String generarNumeroHistoriaClinica() {
        return "HC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}