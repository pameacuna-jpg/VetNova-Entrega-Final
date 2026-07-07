package com.vetnova.inventario.client;

import com.vetnova.inventario.dto.SucursalValidacionDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class SucursalClient {

    private final RestTemplate restTemplate;

    @Value("${sucursales.url:http://localhost:8090/api/v1/sucursales}")
    private String sucursalesUrl;

    public SucursalClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public SucursalValidacionDTO obtenerSucursalPorId(Long idSucursal) {
        String url = sucursalesUrl + "/" + idSucursal;
        return restTemplate.getForObject(url, SucursalValidacionDTO.class);
    }

    public boolean existeSucursal(Long idSucursal) {
        try {
            SucursalValidacionDTO response = obtenerSucursalPorId(idSucursal);
            return response != null && Boolean.TRUE.equals(response.getActiva());
        } catch (Exception e) {
            return false;
        }
    }

    public boolean validarSucursal(Long idSucursal) {
        return existeSucursal(idSucursal);
    }
}