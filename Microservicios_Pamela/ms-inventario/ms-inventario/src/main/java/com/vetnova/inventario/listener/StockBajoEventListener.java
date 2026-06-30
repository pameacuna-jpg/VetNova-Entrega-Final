package com.vetnova.inventario.listener;

import com.vetnova.inventario.dto.NotificacionRequest;
import com.vetnova.inventario.dto.StockBajoEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class StockBajoEventListener {

    private static final Logger logger =
            LoggerFactory.getLogger(StockBajoEventListener.class);

    private final RestTemplate restTemplate;

    @Value("${notificaciones.url}")
    private String notificacionesUrl;

    public StockBajoEventListener(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @EventListener
    public void manejarStockBajo(StockBajoEvent event) {
        try {
            NotificacionRequest request = new NotificacionRequest();
            request.setDestinatario("inventario@vetnova.cl");
            request.setMensaje("ALERTA: El producto " + event.getNombreProducto() + " posee stock bajo.");
            request.setTipo("STOCK_BAJO");
            request.setCanal("EMAIL");
            request.setPrioridad("ALTA");

            restTemplate.postForObject(notificacionesUrl, request, Object.class);

            logger.info("Notificación de stock bajo enviada correctamente para producto: {}",
                    event.getNombreProducto());

        } catch (Exception e) {
            logger.error("Error al enviar notificación de stock bajo: {}", e.getMessage());
        }
    }
}