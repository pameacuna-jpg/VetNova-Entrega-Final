package com.vetnova.atencionclinica.service;

import com.vetnova.atencionclinica.dto.FichaClinicaRequestDTO;
import com.vetnova.atencionclinica.dto.FichaClinicaResponseDTO;
import com.vetnova.atencionclinica.event.AtencionRegistradaEvent;
import com.vetnova.atencionclinica.exception.ResourceNotFoundException;
import com.vetnova.atencionclinica.exception.ServiceUnavailableException;
import com.vetnova.atencionclinica.model.FichaClinica;
import com.vetnova.atencionclinica.repository.FichaClinicaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FichaClinicaService {

    private final FichaClinicaRepository repository;
    private final ApplicationEventPublisher eventPublisher;
    private final RestTemplate restTemplate;

    @Value("${services.mascotas.url:http://localhost:8085}")
    private String mascotasServiceUrl;

    @Transactional
    public FichaClinicaResponseDTO crearFicha(FichaClinicaRequestDTO request) {
        validarMascotaExistente(request.getIdMascota());

        FichaClinica ficha = mapToEntity(request);
        log.info("Creando ficha clínica para Mascota ID: {}", ficha.getIdMascota());
        ficha.setFechaCreacion(LocalDateTime.now());
        FichaClinica nuevaFicha = repository.save(ficha);

        AtencionRegistradaEvent evento = new AtencionRegistradaEvent(
                nuevaFicha.getIdFicha(),
                nuevaFicha.getIdMascota(),
                "REGISTRADA"
        );
        eventPublisher.publishEvent(evento);
        log.info("Evento [AtencionRegistrada] emitido exitosamente.");

        return mapToResponse(nuevaFicha);
    }

    public List<FichaClinica> obtenerTodas() {
        return repository.findAll();
    }

    public FichaClinica buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("La ficha clínica con ID " + id + " no existe."));
    }

    private FichaClinica mapToEntity(FichaClinicaRequestDTO request) {
        FichaClinica ficha = new FichaClinica();
        ficha.setIdMascota(request.getIdMascota());
        ficha.setObservaciones(request.getObservaciones());
        return ficha;
    }

    private FichaClinicaResponseDTO mapToResponse(FichaClinica ficha) {
        FichaClinicaResponseDTO response = new FichaClinicaResponseDTO();
        response.setIdFicha(ficha.getIdFicha());
        response.setIdMascota(ficha.getIdMascota());
        response.setFechaCreacion(ficha.getFechaCreacion());
        response.setObservaciones(ficha.getObservaciones());
        return response;
    }

    private void validarMascotaExistente(Long idMascota) {
        String url = mascotasServiceUrl + "/api/v1/mascotas/" + idMascota;
        try {
            restTemplate.getForEntity(url, Object.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ResourceNotFoundException("La mascota con ID " + idMascota + " no existe.");
            }
            throw new ResourceNotFoundException("No se pudo validar la mascota con ID " + idMascota + ".");
        } catch (ResourceAccessException e) {
            throw new ServiceUnavailableException("El microservicio de Mascotas no se encuentra activo. Operación abortada por integridad.");
        }
    }
}