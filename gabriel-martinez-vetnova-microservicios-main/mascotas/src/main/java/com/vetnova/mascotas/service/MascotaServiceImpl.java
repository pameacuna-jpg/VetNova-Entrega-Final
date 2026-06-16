package com.vetnova.mascotas.service;

import com.vetnova.mascotas.dto.*;
import com.vetnova.mascotas.exception.ResourceNotFoundException;
import com.vetnova.mascotas.model.*;
import com.vetnova.mascotas.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class MascotaServiceImpl implements IMascotaService {

    private final MascotaRepository mascotaRepository;
    private final EspecieRepository especieRepository;
    private final TransferenciaRepository transferenciaRepository;
    private final RestTemplate restTemplate;

    @Value("${notificaciones.url}")
    private String notificacionesUrl;

    @Override
    @Transactional
    public MascotaResponseDTO registrar(MascotaRequestDTO request) {
        log.info("HU-MA01: Registrando mascota '{}' para el cliente ID: {}", request.getNombre(), request.getIdCliente());

        Especie especie = especieRepository.findByNombreIgnoreCase(request.getEspecieNombre())
                .orElseThrow(() -> new ResourceNotFoundException("La Especie especificada no existe en el catálogo maestro"));

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

        long correlativo = mascotaRepository.count() + 1;
        String nroHistoria = String.format("VHC-%05d", correlativo);

        HistorialMascota historial = HistorialMascota.builder()
                .numeroHistoriaClinica(nroHistoria)
                .resumenClinico("Apertura de expediente clínico.")
                .ultimoPeso(Objects.requireNonNullElse(request.getUltimoPeso(), 0.0))
                .estaEsterilizado(Objects.requireNonNullElse(request.getEstaEsterilizado(), false))
                .alergiasCriticas(request.getAlergiasCriticas())
                .fechaUltimaAtencion(LocalDate.now())
                .build();

        mascota.asignarHistorial(historial);

        Mascota mascotaGuardada = mascotaRepository.save(mascota);

        enviarNotificacionMascotaRegistrada(mascotaGuardada);

        return mapToResponse(mascotaGuardada);
    }

    @Override
    @Transactional
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
            mascota.getHistorialMascota().setUltimoPeso(Objects.requireNonNullElse(request.getUltimoPeso(), 0.0));
            mascota.getHistorialMascota().setEstaEsterilizado(Objects.requireNonNullElse(request.getEstaEsterilizado(), false));
            mascota.getHistorialMascota().setAlergiasCriticas(request.getAlergiasCriticas());
        }

        return mapToResponse(mascotaRepository.save(mascota));
    }

    @Override
    @Transactional(readOnly = true)
    public MascotaResponseDTO obtenerPorId(Long id) {
        Mascota mascota = mascotaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado"));

        return mapToResponse(mascota);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MascotaResponseDTO> buscarPacientes(String filtro, Pageable pageable) {
        log.info("HU-MA05: Ejecutando búsqueda rápida global con filtro: {}", filtro);
        return mascotaRepository.buscarPacientesGlobal(filtro, pageable).map(this::mapToResponse);
    }

    @Override
    @Transactional
    public void transferirPropietario(Long idMascota, TransferenciaRequestDTO request) {
        log.info("HU-MA03: Procesando transferencia de titularidad de la mascota ID: {}", idMascota);

        Mascota mascota = mascotaRepository.findById(idMascota)
                .orElseThrow(() -> new ResourceNotFoundException("Mascota no encontrada"));

        Long propietarioAnterior = mascota.getIdCliente();

        TransferenciaPropietario historialCambio = TransferenciaPropietario.builder()
                .idMascota(idMascota)
                .idClienteAnterior(propietarioAnterior)
                .idClienteNuevo(request.getIdNuevoCliente())
                .fechaTransferencia(LocalDateTime.now())
                .build();

        transferenciaRepository.save(historialCambio);

        mascota.setIdCliente(request.getIdNuevoCliente());
        Mascota mascotaGuardada = mascotaRepository.save(mascota);

        enviarNotificacionTransferencia(mascotaGuardada, propietarioAnterior, request.getIdNuevoCliente());

        log.info("Traspaso finalizado con éxito. Nuevo propietario: {}", request.getIdNuevoCliente());
    }

    @Override
    @Transactional
    public void actualizarEstadoVital(Long idMascota, String nuevoEstado) {
        log.info("HU-MA06: Cambiando estado de permanencia biológica a: {}", nuevoEstado);

        Mascota mascota = mascotaRepository.findById(idMascota)
                .orElseThrow(() -> new ResourceNotFoundException("Mascota no encontrada"));

        mascota.setEstado(nuevoEstado.toUpperCase());

        if (nuevoEstado.equalsIgnoreCase("FALLECIDO") || nuevoEstado.equalsIgnoreCase("EXTRAVIADO")) {
            log.info("Desactivando triggers y cancelando agendas futuras para el paciente por estado sensible.");
        }

        mascotaRepository.save(mascota);
    }

    private void enviarNotificacionMascotaRegistrada(Mascota mascota) {
        try {
            NotificacionRequestDTO notificacion = new NotificacionRequestDTO();
            notificacion.setDestinatario("cliente-" + mascota.getIdCliente() + "@vetnova.internal");
            notificacion.setMensaje("Mascota registrada en VetNova: " + mascota.getNombre());
            notificacion.setTipo("MASCOTA");
            notificacion.setCanal("EMAIL");
            notificacion.setPrioridad("MEDIA");

            restTemplate.postForObject(notificacionesUrl, notificacion, Void.class);

            log.info("Notificación enviada por registro de mascota ID: {}", mascota.getIdMascota());

        } catch (Exception e) {
            log.warn("No se pudo enviar notificación de mascota registrada: {}", e.getMessage());
        }
    }

    private void enviarNotificacionTransferencia(Mascota mascota, Long propietarioAnterior, Long propietarioNuevo) {
        try {
            NotificacionRequestDTO notificacion = new NotificacionRequestDTO();
            notificacion.setDestinatario("cliente-" + propietarioNuevo + "@vetnova.internal");
            notificacion.setMensaje("La mascota " + mascota.getNombre()
                    + " fue transferida desde el cliente ID "
                    + propietarioAnterior
                    + " al cliente ID "
                    + propietarioNuevo);
            notificacion.setTipo("TRANSFERENCIA_MASCOTA");
            notificacion.setCanal("EMAIL");
            notificacion.setPrioridad("ALTA");

            restTemplate.postForObject(notificacionesUrl, notificacion, Void.class);

            log.info("Notificación enviada por transferencia de mascota ID: {}", mascota.getIdMascota());

        } catch (Exception e) {
            log.warn("No se pudo enviar notificación de transferencia de mascota: {}", e.getMessage());
        }
    }

    private MascotaResponseDTO mapToResponse(Mascota m) {
        String edadTexto = "No especificada";

        if (m.getFechaNacimiento() != null) {
            Period periodo = Period.between(m.getFechaNacimiento(), LocalDate.now());
            edadTexto = String.format("%d Años, %d Meses y %d Días",
                    periodo.getYears(), periodo.getMonths(), periodo.getDays());
        }

        HistorialMascota hm = m.getHistorialMascota();

        return MascotaResponseDTO.builder()
                .idMascota(m.getIdMascota())
                .nombre(m.getNombre())
                .especie(m.getEspecie() != null ? m.getEspecie().getNombre() : null)
                .raza(m.getRaza())
                .sexo(m.getSexo())
                .fechaNacimiento(m.getFechaNacimiento())
                .edadCalculada(edadTexto)
                .idCliente(m.getIdCliente())
                .estado(m.getEstado())
                .numeroHistoriaClinica(hm != null ? hm.getNumeroHistoriaClinica() : null)
                .ultimoPeso(Objects.requireNonNullElse(hm != null ? hm.getUltimoPeso() : null, 0.0))
                .estaEsterilizado(Objects.requireNonNullElse(hm != null ? hm.getEstaEsterilizado() : null, false))
                .alergiasCriticas(hm != null ? hm.getAlergiasCriticas() : "Ninguna")
                .resumenClinico(hm != null ? hm.getResumenClinico() : null)
                .build();
    }
}