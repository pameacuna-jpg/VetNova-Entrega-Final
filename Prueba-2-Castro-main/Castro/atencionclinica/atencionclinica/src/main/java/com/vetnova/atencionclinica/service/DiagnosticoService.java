package com.vetnova.atencionclinica.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vetnova.atencionclinica.dto.ConsumoInventarioRequest;
import com.vetnova.atencionclinica.dto.DiagnosticoRequestDTO;
import com.vetnova.atencionclinica.dto.DiagnosticoResponseDTO;
import com.vetnova.atencionclinica.dto.RecetaRequestDTO;
import com.vetnova.atencionclinica.event.CertificadoEmitidoEvent;
import com.vetnova.atencionclinica.event.RecetaEmitidaEvent;
import com.vetnova.atencionclinica.exception.BusinessException;
import com.vetnova.atencionclinica.exception.ResourceNotFoundException;
import com.vetnova.atencionclinica.exception.ServiceUnavailableException;
import com.vetnova.atencionclinica.model.Diagnostico;
import com.vetnova.atencionclinica.model.FichaClinica;
import com.vetnova.atencionclinica.repository.DiagnosticoRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class DiagnosticoService {

    private static final Logger logger = LoggerFactory.getLogger(DiagnosticoService.class);

    private final DiagnosticoRepository repository;
    private final ApplicationEventPublisher eventPublisher;
    private final RestTemplate restTemplate;

    @Value("${services.inventario.url:http://localhost:8087}")
    private String inventarioServiceUrl;

    private static final Long ID_SUCURSAL_POR_DEFECTO = 1L; // Decisión: atencionclinica no maneja sucursal propia aún; se usa la sucursal principal hasta que exista el vínculo (ver punto opcional Agenda→Sucursales)

    @Transactional(readOnly = true)
    public DiagnosticoResponseDTO buscarPorId(Long id) {
        Diagnostico diagnostico = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("La atención con ID " + id + " no existe."));
        return mapToResponse(diagnostico);
    }

    @Transactional
    public DiagnosticoResponseDTO registrarDiagnostico(DiagnosticoRequestDTO request) {
        Diagnostico diagnostico = mapToEntity(request);
        Diagnostico diagnosticoGuardado = repository.save(diagnostico);

        logger.info("Diagnóstico registrado correctamente. ID: {}",
                diagnosticoGuardado.getIdDiagnostico());

        return mapToResponse(diagnosticoGuardado);
    }

    @Transactional
    public DiagnosticoResponseDTO registrarTratamiento(Long id, String tratamiento) {
        Diagnostico atencion = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("La atención con ID " + id + " no existe."));

        atencion.setTratamiento(tratamiento);
        Diagnostico actualizado = repository.save(atencion);

        logger.info("Tratamiento registrado para diagnóstico ID: {}",
                actualizado.getIdDiagnostico());

        return mapToResponse(actualizado);
    }

    @Transactional
    public DiagnosticoResponseDTO emitirReceta(Long id, RecetaRequestDTO request) {
        Diagnostico atencion = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("La atención con ID " + id + " no existe."));

        atencion.setRecetaMedica(request.getRecetaMedica());
        Diagnostico actualizado = repository.save(atencion);

        if (request.getIdProducto() != null) {
            descontarInsumo(actualizado, request.getIdProducto(), request.getCantidad());
        }

        Long idMascota = obtenerIdMascota(actualizado);
        Long idCliente = obtenerIdCliente(actualizado);
        RecetaEmitidaEvent evento = new RecetaEmitidaEvent(
                actualizado.getIdDiagnostico(),
                actualizado.getIdVeterinario(),
                idMascota,
                idCliente,
                actualizado.getRecetaMedica()
        );
        eventPublisher.publishEvent(evento);

        logger.info("Evento RecetaEmitida publicado. Diagnóstico ID: {}, Mascota ID: {}",
                actualizado.getIdDiagnostico(),
                idMascota);

        return mapToResponse(actualizado);
    }

    @SuppressWarnings("unchecked")
    private void descontarInsumo(Diagnostico diagnostico, Long idProducto, Integer cantidad) {
        int cantidadAConsumir = cantidad != null ? cantidad : 1;

        String urlDisponibilidad = inventarioServiceUrl + "/api/v1/inventario/disponibilidad/" + idProducto + "?cantidad=" + cantidadAConsumir;
        try {
            var response = restTemplate.getForEntity(urlDisponibilidad, Map.class);
            Map<String, Object> body = response.getBody();

            boolean disponible = body != null && Boolean.TRUE.equals(body.get("disponible"));
            boolean activo = body != null && Boolean.TRUE.equals(body.get("activo"));

            if (!disponible || !activo) {
                String mensaje = body != null && body.get("mensaje") != null
                        ? body.get("mensaje").toString()
                        : "El insumo con ID " + idProducto + " no está disponible.";
                throw new BusinessException(mensaje);
            }
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == org.springframework.http.HttpStatus.NOT_FOUND) {
                throw new ResourceNotFoundException("El insumo con ID " + idProducto + " no existe.");
            }
            throw new ResourceNotFoundException("No se pudo validar el insumo con ID " + idProducto + ".");
        } catch (ResourceAccessException e) {
            throw new ServiceUnavailableException("El microservicio de Inventario no se encuentra activo. Operación abortada por integridad.");
        }

        ConsumoInventarioRequest consumo = new ConsumoInventarioRequest(
                idProducto,
                ID_SUCURSAL_POR_DEFECTO,
                cantidadAConsumir,
                "ATENCION_CLINICA",
                diagnostico.getIdDiagnostico(),
                "Consumo por receta emitida en diagnóstico Nº" + diagnostico.getIdDiagnostico()
        );

        try {
            restTemplate.postForEntity(inventarioServiceUrl + "/api/v1/inventario/consumos", consumo, Object.class);
        } catch (HttpClientErrorException e) {
            throw new BusinessException("No se pudo registrar el consumo del insumo con ID " + idProducto + ": " + e.getMessage());
        } catch (ResourceAccessException e) {
            throw new ServiceUnavailableException("El microservicio de Inventario no se encuentra activo. Operación abortada por integridad.");
        }
    }

    @Transactional
    public DiagnosticoResponseDTO emitirCertificado(Long id, String detalleCertificado) {
        Diagnostico atencion = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("La atención con ID " + id + " no existe."));

        atencion.setDetalleCertificado(detalleCertificado);
        Diagnostico actualizado = repository.save(atencion);

        Long idMascota = obtenerIdMascota(actualizado);
        Long idCliente = obtenerIdCliente(actualizado);
        CertificadoEmitidoEvent evento = new CertificadoEmitidoEvent(
                actualizado.getIdDiagnostico(),
                actualizado.getIdVeterinario(),
                idMascota,
                idCliente,
                actualizado.getDetalleCertificado()
        );
        eventPublisher.publishEvent(evento);

        logger.info("Evento CertificadoEmitido publicado. Diagnóstico ID: {}, Mascota ID: {}",
                actualizado.getIdDiagnostico(),
                idMascota);

        return mapToResponse(actualizado);
    }

    private Diagnostico mapToEntity(DiagnosticoRequestDTO request) {
        Diagnostico diagnostico = new Diagnostico();
        diagnostico.setDescripcion(request.getDescripcion());
        diagnostico.setTratamiento(request.getTratamiento());
        diagnostico.setRecetaMedica(request.getRecetaMedica());
        diagnostico.setDetalleCertificado(request.getDetalleCertificado());
        diagnostico.setIdVeterinario(request.getIdVeterinario());

        FichaClinica fichaClinica = new FichaClinica();
        fichaClinica.setIdFicha(request.getIdFicha());
        diagnostico.setFichaClinica(fichaClinica);

        return diagnostico;
    }

    private DiagnosticoResponseDTO mapToResponse(Diagnostico diagnostico) {
        Long idFicha = null;
        Long idMascota = null;

        if (diagnostico.getFichaClinica() != null) {
            idFicha = diagnostico.getFichaClinica().getIdFicha();
            idMascota = diagnostico.getFichaClinica().getIdMascota();
        }

        return DiagnosticoResponseDTO.builder()
                .idDiagnostico(diagnostico.getIdDiagnostico())
                .descripcion(diagnostico.getDescripcion())
                .tratamiento(diagnostico.getTratamiento())
                .recetaMedica(diagnostico.getRecetaMedica())
                .detalleCertificado(diagnostico.getDetalleCertificado())
                .fecha(diagnostico.getFecha())
                .idVeterinario(diagnostico.getIdVeterinario())
                .idFicha(idFicha)
                .idMascota(idMascota)
                .build();
    }

    private Long obtenerIdMascota(Diagnostico diagnostico) {
        if (diagnostico.getFichaClinica() != null) {
            return diagnostico.getFichaClinica().getIdMascota();
        }
        return null;
    }

    private Long obtenerIdCliente(Diagnostico diagnostico) {
        if (diagnostico.getFichaClinica() != null) {
            return diagnostico.getFichaClinica().getIdCliente();
        }
        return null;
    }
}