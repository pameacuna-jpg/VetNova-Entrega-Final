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
    public void manejarStockBajo(StockBajoEvent event) {

        try {
            NotificacionRequest request = new NotificacionRequest();

            request.setDestino("INTERNA");
            request.setIdCliente(null);
            request.setDestinatario("inventario@vetnova.cl");

            request.setMensaje(
                    "ALERTA DE STOCK BAJO: El producto "
                            + event.getNombreProducto()
                            + " tiene un stock actual de "
                            + event.getStockActual()
                            + " unidades y un stock mínimo de "
                            + event.getStockMinimo()
                            + " unidades. Sucursal ID: "
                            + event.getIdSucursal()
                            + "."
            );

            request.setTipo("STOCK_BAJO");
            request.setCanal("EMAIL");
            request.setPrioridad("ALTA");

            restTemplate.postForEntity(
                    notificacionesUrl,
                    request,
                    Object.class
            );

            log.info(
                    "Notificación interna de stock bajo enviada. Producto: {}, sucursal: {}, stock actual: {}",
                    event.getNombreProducto(),
                    event.getIdSucursal(),
                    event.getStockActual()
            );

        } catch (Exception ex) {
            log.error(
                    "No se pudo enviar la notificación de stock bajo. Producto: {}, sucursal: {}, error: {}",
                    event.getNombreProducto(),
                    event.getIdSucursal(),
                    ex.getMessage(),
                    ex
            );
        }
    }
}