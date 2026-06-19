package com.vetnova.inventario.service;

import com.vetnova.inventario.dto.StockBajoEvent; // Importación del evento mapeado en el paquete dto
import com.vetnova.inventario.model.MovimientoInventario;
import com.vetnova.inventario.model.Producto;
import com.vetnova.inventario.repository.MovimientoInventarioRepository;
import com.vetnova.inventario.repository.ProductoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio de negocio para la gestión y control de flujos de inventario.
 * Implementa el estándar CSR y la inyección inmutable por constructor exigida.
 */
@Service
public class MovimientoInventarioService {

    private static final Logger logger =
            LoggerFactory.getLogger(MovimientoInventarioService.class);

    // Atributos de dependencias declarados como final (Inmutabilidad obligatoria)
    private final MovimientoInventarioRepository movimientoRepository;
    private final ProductoRepository productoRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * UN ÚNICO CONSTRUCTOR EXPLÍCITO
     * Resuelve el error de sobrecarga ambigua y cumple con el Mandato de Alineación Técnica.
     */
    public MovimientoInventarioService(MovimientoInventarioRepository movimientoRepository,
                                    ProductoRepository productoRepository,
                                    ApplicationEventPublisher eventPublisher) {
        this.movimientoRepository = movimientoRepository;
        this.productoRepository = productoRepository;
        this.eventPublisher = eventPublisher;
    }

    public List<MovimientoInventario> listarMovimientos() {
        return movimientoRepository.findAll();
    }

    public MovimientoInventario buscarPorId(Long id) {
        return movimientoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movimiento no encontrado con ID: " + id));
    }

    public MovimientoInventario registrarMovimiento(MovimientoInventario movimiento) {

        logger.info("Registrando movimiento de inventario. Producto ID: {}, Tipo: {}, Cantidad: {}",
                movimiento.getIdProducto(),
                movimiento.getTipoMovimiento(),
                movimiento.getCantidad());

        Producto producto = productoRepository.findById(movimiento.getIdProducto())
                .orElseThrow(() -> new RuntimeException(
                        "Producto no encontrado con ID: " + movimiento.getIdProducto()));

        String tipo = movimiento.getTipoMovimiento().toUpperCase();

        switch (tipo) {
            case "ENTRADA":
                producto.setStockActual(producto.getStockActual() + movimiento.getCantidad());
                break;

            case "SALIDA":
                if (producto.getStockActual() < movimiento.getCantidad()) {
                    throw new RuntimeException("Stock insuficiente para realizar la salida");
                }
                producto.setStockActual(producto.getStockActual() - movimiento.getCantidad());
                break;

            case "AJUSTE":
                producto.setStockActual(movimiento.getCantidad());
                break;

            default:
                throw new RuntimeException("Tipo de movimiento inválido");
        }

        productoRepository.save(producto);

        logger.info("Stock actualizado para producto {}. Stock actual: {}",
                producto.getNombre(),
                producto.getStockActual());

        // Evaluación del umbral crítico de abastecimiento
        if (producto.getStockActual() <= producto.getStockMinimo()) {
            publicarEventoStockBajo(producto, movimiento.getIdSucursal());
        }

        return movimientoRepository.save(movimiento);
    }

    /**
     * Despacha un evento interno asíncrono dentro del contexto de Spring.
     * Esto elimina la dependencia síncrona muerta de RestTemplate.
     */
    private void publicarEventoStockBajo(Producto producto, Long idSucursal) {
        try {
            logger.warn("Emitiendo de forma desacoplada StockBajoEvent para el producto: {}", producto.getNombre());
            
            // Instanciación del evento DTO pasando el 'source' (this) y los datos requeridos
            StockBajoEvent evento = new StockBajoEvent(
                    this,
                    producto.getNombre(),
                    producto.getStockActual(),
                    producto.getStockMinimo(),
                    idSucursal
            );

            // Uso operativo de la variable inyectada (Resuelve el warning 'is not used')
            eventPublisher.publishEvent(evento);

        } catch (Exception e) {
            logger.error("Error crítico al despachar el evento de inventario: {}", e.getMessage());
        }
    }

    public List<MovimientoInventario> buscarPorProducto(Long idProducto) {
        return movimientoRepository.findByIdProducto(idProducto);
    }

    public List<MovimientoInventario> buscarPorSucursal(Long idSucursal) {
        return movimientoRepository.findByIdSucursal(idSucursal);
    }

    public List<MovimientoInventario> buscarPorTipo(String tipo) {
        return movimientoRepository.findByTipoMovimientoIgnoreCase(tipo);
    }
}