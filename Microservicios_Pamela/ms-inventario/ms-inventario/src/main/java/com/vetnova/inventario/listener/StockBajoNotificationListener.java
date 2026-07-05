package com.vetnova.inventario.listener;

import com.vetnova.inventario.dto.NotificacionRequest;
import com.vetnova.inventario.dto.StockBajoEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockBajoNotificationListener {

    private final RestTemplate restTemplate;

    @Value("${notificaciones.url:http://localhost:8089/api/v1/notificaciones}")
    private String notificacionesUrl;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleStockBajo(StockBajoEvent event) {
        try {
            NotificacionRequest request = new NotificacionRequest(
                    "inventario-sucursal-" + event.getIdSucursal(),
                    "Stock bajo de " + event.getNombreProducto() + " (actual: " + event.getStockActual() + ", mínimo: " + event.getStockMinimo() + ")",
                    "STOCK_BAJO",
                    "EMAIL",
                    "ALTA"
            );

            restTemplate.postForEntity(notificacionesUrl, request, String.class);
            log.info("Notificación de stock bajo enviada para la sucursal {}", event.getIdSucursal());
        } catch (Exception ex) {
            log.warn("No se pudo enviar la notificación de stock bajo: {}", ex.getMessage());
        }
    }
}
