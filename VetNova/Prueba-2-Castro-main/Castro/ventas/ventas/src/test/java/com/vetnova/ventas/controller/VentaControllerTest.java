package com.vetnova.ventas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vetnova.ventas.dto.VentaRequestDTO;
import com.vetnova.ventas.model.Venta;
import com.vetnova.ventas.service.VentaService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VentaController.class)
class VentaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VentaService ventaService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registrarVenta_debeRetornarCreated() throws Exception {

        Mockito.when(ventaService.registrarVenta(any(Venta.class)))
                .thenReturn(crearVenta());

        mockMvc.perform(post("/api/v1/ventas/registrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(crearRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
    }

    @Test
    void procesarPago_debeRetornarOk() throws Exception {

        Venta venta = crearVenta();
        venta.setEstado("PAGADA");

        Mockito.when(ventaService.procesarPago(1L))
                .thenReturn(venta);

        mockMvc.perform(put("/api/v1/ventas/1/pagar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("PAGADA"));
    }

    @Test
    void registrarDevolucion_debeRetornarOk() throws Exception {

        Venta venta = crearVenta();
        venta.setEstado("DEVUELTA");

        Mockito.when(ventaService.registrarDevolucion(1L))
                .thenReturn(venta);

        mockMvc.perform(put("/api/v1/ventas/1/devolucion"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("DEVUELTA"));
    }

    @Test
    void emitirBoleta_debeRetornarOk() throws Exception {

        Mockito.when(ventaService.emitirBoleta(1L))
                .thenReturn(crearVenta());

        mockMvc.perform(get("/api/v1/ventas/1/boleta"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void listarVentas_debeRetornarLista() throws Exception {

        Mockito.when(ventaService.obtenerTodasLasVentas())
                .thenReturn(List.of(crearVenta()));

        mockMvc.perform(get("/api/v1/ventas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void obtenerVenta_debeRetornarVenta() throws Exception {

        Mockito.when(ventaService.obtenerVentaPorId(1L))
                .thenReturn(crearVenta());

        mockMvc.perform(get("/api/v1/ventas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCliente").value(10));
    }

    @Test
    void registrarVenta_conDatosInvalidos_debeRetornarBadRequest() throws Exception {

        VentaRequestDTO request = new VentaRequestDTO();

        mockMvc.perform(post("/api/v1/ventas/registrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    private VentaRequestDTO crearRequest() {

        VentaRequestDTO dto = new VentaRequestDTO();
        dto.setIdCliente(10L);
        dto.setIdProducto(20L);
        dto.setCantidad(2);
        dto.setMontoTotal(50000.0);

        return dto;
    }

    private Venta crearVenta() {

        Venta venta = new Venta();
        venta.setId(1L);
        venta.setIdCliente(10L);
        venta.setIdProducto(20L);
        venta.setCantidad(2);
        venta.setMontoTotal(50000.0);
        venta.setEstado("PENDIENTE");
        venta.setFechaVenta(LocalDateTime.now());

        return venta;
    }
}