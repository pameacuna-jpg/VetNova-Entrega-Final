package com.vetnova.notificaciones.client;

import com.vetnova.notificaciones.dto.ClienteResponseDTO;
import com.vetnova.notificaciones.exception.ClienteNoEncontradoException;
import com.vetnova.notificaciones.exception.MicroservicioNoDisponibleException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class ClienteClient {

    private final RestTemplate restTemplate;

    @Value("${clientes.url}")
    private String clientesUrl;

    public ClienteResponseDTO obtenerClientePorId(Long idCliente) {
        try {
            return restTemplate.getForObject(
                    clientesUrl + "/" + idCliente,
                    ClienteResponseDTO.class
            );
        } catch (HttpClientErrorException.NotFound e) {
            throw new ClienteNoEncontradoException("No existe un cliente registrado con id " + idCliente);
        } catch (ResourceAccessException e) {
            throw new MicroservicioNoDisponibleException("El microservicio Clientes no está disponible");
        } catch (Exception e) {
            throw new MicroservicioNoDisponibleException("No fue posible consultar el microservicio Clientes");
        }
    }
}