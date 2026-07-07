package com.vetnova.inventario.client;

import com.vetnova.inventario.dto.SucursalValidacionDTO;
import com.vetnova.inventario.exception.MicroservicioNoDisponibleException;
import com.vetnova.inventario.exception.SucursalNoEncontradaException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Component
public class SucursalClient {

    private final RestTemplate restTemplate;

    @Value("${sucursales.url}")
    private String sucursalesUrl;

    public SucursalClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void validarSucursal(Long idSucursal) {
        try {
            SucursalValidacionDTO response = restTemplate.getForObject(
                    sucursalesUrl + "/" + idSucursal + "/validar",
                    SucursalValidacionDTO.class
            );

            if (response == null || !response.isExiste()) {
                throw new SucursalNoEncontradaException("La sucursal indicada no existe");
            }

            if (!response.isActiva()) {
                throw new SucursalNoEncontradaException("La sucursal indicada no está activa");
            }

        } catch (SucursalNoEncontradaException e) {
            throw e;
        } catch (ResourceAccessException e) {
            throw new MicroservicioNoDisponibleException("El microservicio Sucursales no está disponible");
        } catch (Exception e) {
            throw new MicroservicioNoDisponibleException("No fue posible validar la sucursal");
        }
    }
}