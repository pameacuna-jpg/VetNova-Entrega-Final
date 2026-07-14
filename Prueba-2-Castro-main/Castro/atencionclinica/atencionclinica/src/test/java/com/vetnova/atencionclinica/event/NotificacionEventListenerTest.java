package com.vetnova.atencionclinica.event;

import com.vetnova.atencionclinica.dto.NotificacionRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificacionEventListenerTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private NotificacionEventListener listener;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(listener, "notificacionesUrl", "http://localhost:8089/api/v1/notificaciones");
    }

    @Test
    void handleRecetaEmitida_debeEnviarNotificacionConDestinoCliente() {
        RecetaEmitidaEvent event = new RecetaEmitidaEvent(1L, 10L, 20L, 55L, "Amoxicilina 500mg");

        listener.handleRecetaEmitida(event);

        ArgumentCaptor<NotificacionRequest> captor = ArgumentCaptor.forClass(NotificacionRequest.class);
        verify(restTemplate).postForEntity(eq("http://localhost:8089/api/v1/notificaciones"), captor.capture(), eq(String.class));

        NotificacionRequest request = captor.getValue();
        assertEquals("CLIENTE", request.getDestino());
        assertEquals(55L, request.getIdCliente());
        assertEquals("ATENCION_CLINICA", request.getTipo());
        assertEquals("EMAIL", request.getCanal());
        assertEquals("ALTA", request.getPrioridad());
    }

    @Test
    void handleRecetaEmitida_siNotificacionesResponde404_noDebeLanzarExcepcion() {
        RecetaEmitidaEvent event = new RecetaEmitidaEvent(1L, 10L, 20L, 55L, "Amoxicilina 500mg");
        when(restTemplate.postForEntity(any(String.class), any(), eq(String.class)))
                .thenThrow(HttpClientErrorException.NotFound.class);

        assertDoesNotThrow(() -> listener.handleRecetaEmitida(event));
    }

    @Test
    void handleRecetaEmitida_siNotificacionesEstaCaida_noDebeLanzarExcepcion() {
        RecetaEmitidaEvent event = new RecetaEmitidaEvent(1L, 10L, 20L, 55L, "Amoxicilina 500mg");
        when(restTemplate.postForEntity(any(String.class), any(), eq(String.class)))
                .thenThrow(new ResourceAccessException("Servicio caído"));

        assertDoesNotThrow(() -> listener.handleRecetaEmitida(event));
    }

    @Test
    void handleRecetaEmitida_siFallaGenericoDeRestClient_noDebeLanzarExcepcion() {
        RecetaEmitidaEvent event = new RecetaEmitidaEvent(1L, 10L, 20L, 55L, "Amoxicilina 500mg");
        when(restTemplate.postForEntity(any(String.class), any(), eq(String.class)))
                .thenThrow(new RestClientException("Error genérico"));

        assertDoesNotThrow(() -> listener.handleRecetaEmitida(event));
    }

    @Test
    void handleCertificadoEmitido_debeEnviarNotificacionConDestinoCliente() {
        CertificadoEmitidoEvent event = new CertificadoEmitidoEvent(1L, 10L, 20L, 55L, "Certificado de salud");

        listener.handleCertificadoEmitido(event);

        ArgumentCaptor<NotificacionRequest> captor = ArgumentCaptor.forClass(NotificacionRequest.class);
        verify(restTemplate).postForEntity(eq("http://localhost:8089/api/v1/notificaciones"), captor.capture(), eq(String.class));

        NotificacionRequest request = captor.getValue();
        assertEquals("CLIENTE", request.getDestino());
        assertEquals(55L, request.getIdCliente());
        assertEquals("ATENCION_CLINICA", request.getTipo());
        assertEquals("MEDIA", request.getPrioridad());
    }

    @Test
    void handleCertificadoEmitido_siNotificacionesResponde404_noDebeLanzarExcepcion() {
        CertificadoEmitidoEvent event = new CertificadoEmitidoEvent(1L, 10L, 20L, 55L, "Certificado de salud");
        when(restTemplate.postForEntity(any(String.class), any(), eq(String.class)))
                .thenThrow(HttpClientErrorException.NotFound.class);

        assertDoesNotThrow(() -> listener.handleCertificadoEmitido(event));
    }

    @Test
    void handleCertificadoEmitido_siNotificacionesEstaCaida_noDebeLanzarExcepcion() {
        CertificadoEmitidoEvent event = new CertificadoEmitidoEvent(1L, 10L, 20L, 55L, "Certificado de salud");
        when(restTemplate.postForEntity(any(String.class), any(), eq(String.class)))
                .thenThrow(new ResourceAccessException("Servicio caído"));

        assertDoesNotThrow(() -> listener.handleCertificadoEmitido(event));
    }

    @Test
    void handleCertificadoEmitido_siFallaGenericoDeRestClient_noDebeLanzarExcepcion() {
        CertificadoEmitidoEvent event = new CertificadoEmitidoEvent(1L, 10L, 20L, 55L, "Certificado de salud");
        when(restTemplate.postForEntity(any(String.class), any(), eq(String.class)))
                .thenThrow(new RestClientException("Error genérico"));

        assertDoesNotThrow(() -> listener.handleCertificadoEmitido(event));
    }
}
