package com.vetnova.inventario.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vetnova.inventario.dto.ConsumoInventarioRequestDTO;
import com.vetnova.inventario.dto.ConsumoInventarioResponseDTO;
import com.vetnova.inventario.dto.DisponibilidadProductoResponseDTO;
import com.vetnova.inventario.enums.OrigenConsumo;
import com.vetnova.inventario.service.InventarioIntegracionService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InventarioIntegracionController.class)
class InventarioIntegracionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InventarioIntegracionService inventarioIntegracionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void consultarDisponibilidad_debeRetornarOkConDatos() throws Exception {
        DisponibilidadProductoResponseDTO response = DisponibilidadProductoResponseDTO.builder()
                .idProducto(1L)
                .nombreProducto("Vacuna Rabia")
                .stockActual(10)
                .cantidadSolicitada(2)
                .disponible(true)
                .activo(true)
                .mensaje("Producto disponible")
                .build();

        Mockito.when(inventarioIntegracionService.consultarDisponibilidad(eq(1L), eq(2)))
                .thenReturn(response);

        mockMvc.perform(get("/api/v1/inventario/disponibilidad/1")
                        .param("cantidad", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idProducto").value(1L))
                .andExpect(jsonPath("$.disponible").value(true))
                .andExpect(jsonPath("$.mensaje").value("Producto disponible"));
    }

    @Test
    void consultarDisponibilidad_conCantidadNoPositiva_debeRetornarBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/inventario/disponibilidad/1")
                        .param("cantidad", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registrarConsumo_debeRetornarCreatedConDatos() throws Exception {
        ConsumoInventarioRequestDTO request = new ConsumoInventarioRequestDTO(
                1L, 1L, 2, OrigenConsumo.VENTA, 10L, "Venta Nº10"
        );

        ConsumoInventarioResponseDTO response = ConsumoInventarioResponseDTO.builder()
                .idMovimiento(50L)
                .idProducto(1L)
                .idSucursal(1L)
                .cantidadConsumida(2)
                .stockAnterior(10)
                .stockActual(8)
                .origen(OrigenConsumo.VENTA)
                .idReferencia(10L)
                .mensaje("Consumo registrado correctamente desde VENTA")
                .build();

        Mockito.when(inventarioIntegracionService.registrarConsumo(any(ConsumoInventarioRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/inventario/consumos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idMovimiento").value(50L))
                .andExpect(jsonPath("$.stockActual").value(8));
    }

    @Test
    void registrarConsumo_conRequestInvalido_debeRetornarBadRequest() throws Exception {
        ConsumoInventarioRequestDTO request = new ConsumoInventarioRequestDTO(
                null, null, null, null, null, null
        );

        mockMvc.perform(post("/api/v1/inventario/consumos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
