package com.vetnova.inventario.client;

import com.vetnova.inventario.dto.SucursalValidacionDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SucursalClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private SucursalClient sucursalClient;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(sucursalClient, "sucursalesUrl", "http://localhost:8090/api/v1/sucursales");
    }

    @Test
    void obtenerSucursalPorId_debeRetornarElDtoDelRestTemplate() {
        SucursalValidacionDTO dto = new SucursalValidacionDTO(1L, "Sucursal Centro", "Av. Siempre Viva 123", true);
        when(restTemplate.getForObject(eq("http://localhost:8090/api/v1/sucursales/1"), eq(SucursalValidacionDTO.class)))
                .thenReturn(dto);

        SucursalValidacionDTO resultado = sucursalClient.obtenerSucursalPorId(1L);

        assertTrue(resultado.getActiva());
    }

    @Test
    void existeSucursal_siEstaActiva_debeRetornarTrue() {
        SucursalValidacionDTO dto = new SucursalValidacionDTO(1L, "Sucursal Centro", "Av. Siempre Viva 123", true);
        when(restTemplate.getForObject(eq("http://localhost:8090/api/v1/sucursales/1"), eq(SucursalValidacionDTO.class)))
                .thenReturn(dto);

        assertTrue(sucursalClient.existeSucursal(1L));
    }

    @Test
    void existeSucursal_siEstaInactiva_debeRetornarFalse() {
        SucursalValidacionDTO dto = new SucursalValidacionDTO(1L, "Sucursal Centro", "Av. Siempre Viva 123", false);
        when(restTemplate.getForObject(eq("http://localhost:8090/api/v1/sucursales/1"), eq(SucursalValidacionDTO.class)))
                .thenReturn(dto);

        assertFalse(sucursalClient.existeSucursal(1L));
    }

    @Test
    void existeSucursal_siLaRespuestaEsNula_debeRetornarFalse() {
        when(restTemplate.getForObject(eq("http://localhost:8090/api/v1/sucursales/1"), eq(SucursalValidacionDTO.class)))
                .thenReturn(null);

        assertFalse(sucursalClient.existeSucursal(1L));
    }

    @Test
    void existeSucursal_siOcurreExcepcion_debeRetornarFalse() {
        when(restTemplate.getForObject(eq("http://localhost:8090/api/v1/sucursales/1"), eq(SucursalValidacionDTO.class)))
                .thenThrow(new ResourceAccessException("Servicio caído"));

        assertFalse(sucursalClient.existeSucursal(1L));
    }

    @Test
    void validarSucursal_debeDelegarEnExisteSucursal() {
        SucursalValidacionDTO dto = new SucursalValidacionDTO(1L, "Sucursal Centro", "Av. Siempre Viva 123", true);
        when(restTemplate.getForObject(eq("http://localhost:8090/api/v1/sucursales/1"), eq(SucursalValidacionDTO.class)))
                .thenReturn(dto);

        assertTrue(sucursalClient.validarSucursal(1L));
    }
}
