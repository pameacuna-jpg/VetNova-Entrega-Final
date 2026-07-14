package com.vetnova.inventario.listener;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.vetnova.inventario.dto.NotificacionRequest;
import com.vetnova.inventario.dto.StockBajoEvent;

@ExtendWith(MockitoExtension.class)
class StockBajoEventListenerTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private StockBajoEventListener listener;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(listener, "notificacionesUrl", "http://localhost:8089/api/v1/notificaciones");
    }

    @Test
    void manejarStockBajo_debeEnviarNotificacionInternaConLosDatosDelEvento() {
        StockBajoEvent event = new StockBajoEvent(this, "Vacuna Rabia", 2, 5, 1L);

        listener.manejarStockBajo(event);

        ArgumentCaptor<NotificacionRequest> captor = ArgumentCaptor.forClass(NotificacionRequest.class);
        verify(restTemplate).postForObject(eq("http://localhost:8089/api/v1/notificaciones"), captor.capture(), eq(Object.class));

        NotificacionRequest request = captor.getValue();
        assertEquals("INTERNA", request.getDestino());
        assertNull(request.getIdCliente());
        assertEquals("inventario@vetnova.cl", request.getDestinatario());
        assertEquals("STOCK_BAJO", request.getTipo());
        assertEquals("EMAIL", request.getCanal());
        assertEquals("ALTA", request.getPrioridad());
        assertEquals(
                "ALERTA DE STOCK BAJO: El producto Vacuna Rabia posee stock actual de 2 unidades, con un stock mínimo de 5 unidades. Sucursal ID: 1.",
                request.getMensaje()
        );
    }

    @Test
    void manejarStockBajo_siNotificacionesFalla_noDebeLanzarExcepcion() {
        StockBajoEvent event = new StockBajoEvent(this, "Vacuna Rabia", 2, 5, 1L);

        when(restTemplate.postForObject(any(String.class), any(), eq(Object.class)))
                .thenThrow(new ResourceAccessException("Servicio caído"));

        assertDoesNotThrow(() -> listener.manejarStockBajo(event));
    }
}