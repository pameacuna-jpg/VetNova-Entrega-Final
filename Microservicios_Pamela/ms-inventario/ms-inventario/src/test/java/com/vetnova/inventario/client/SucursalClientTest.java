package com.vetnova.inventario.client;

import com.vetnova.inventario.dto.SucursalValidacionDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SucursalClientTest {

    private RestTemplate restTemplate;
    private SucursalClient sucursalClient;

    @BeforeEach
    void setUp() {
        restTemplate = mock(RestTemplate.class);
        sucursalClient = new SucursalClient(restTemplate);
        ReflectionTestUtils.setField(
                sucursalClient,
                "sucursalesUrl",
                "http://localhost:8090/api/v1/sucursales"
        );
    }

    @Test
    void obtenerSucursalPorId_deberiaRetornarSucursal() {
        SucursalValidacionDTO dto = new SucursalValidacionDTO(
                1L,
                "Sucursal Central",
                "Concepción",
                true
        );

        when(restTemplate.getForObject(
                "http://localhost:8090/api/v1/sucursales/1",
                SucursalValidacionDTO.class
        )).thenReturn(dto);

        SucursalValidacionDTO resultado = sucursalClient.obtenerSucursalPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdSucursal());
        assertEquals("Sucursal Central", resultado.getNombre());
        assertEquals("Concepción", resultado.getDireccion());
        assertTrue(resultado.getActiva());

        verify(restTemplate).getForObject(
                "http://localhost:8090/api/v1/sucursales/1",
                SucursalValidacionDTO.class
        );
    }

    @Test
    void existeSucursal_cuandoSucursalActiva_deberiaRetornarTrue() {
        SucursalValidacionDTO dto = new SucursalValidacionDTO(
                1L,
                "Sucursal Central",
                "Concepción",
                true
        );

        when(restTemplate.getForObject(anyString(), eq(SucursalValidacionDTO.class)))
                .thenReturn(dto);

        boolean resultado = sucursalClient.existeSucursal(1L);

        assertTrue(resultado);
    }

    @Test
    void existeSucursal_cuandoSucursalInactiva_deberiaRetornarFalse() {
        SucursalValidacionDTO dto = new SucursalValidacionDTO(
                1L,
                "Sucursal Central",
                "Concepción",
                false
        );

        when(restTemplate.getForObject(anyString(), eq(SucursalValidacionDTO.class)))
                .thenReturn(dto);

        boolean resultado = sucursalClient.existeSucursal(1L);

        assertFalse(resultado);
    }

    @Test
    void existeSucursal_cuandoRestTemplateLanzaError_deberiaRetornarFalse() {
        when(restTemplate.getForObject(anyString(), eq(SucursalValidacionDTO.class)))
                .thenThrow(new RuntimeException("Error de conexión"));

        boolean resultado = sucursalClient.existeSucursal(1L);

        assertFalse(resultado);
    }

    @Test
    void validarSucursal_deberiaUsarExisteSucursal() {
        SucursalValidacionDTO dto = new SucursalValidacionDTO(
                1L,
                "Sucursal Central",
                "Concepción",
                true
        );

        when(restTemplate.getForObject(anyString(), eq(SucursalValidacionDTO.class)))
                .thenReturn(dto);

        boolean resultado = sucursalClient.validarSucursal(1L);

        assertTrue(resultado);
    }
}