package com.vetnova.inventario.service;

import com.vetnova.inventario.client.SucursalClient;
import com.vetnova.inventario.dto.StockBajoEvent;
import com.vetnova.inventario.model.MovimientoInventario;
import com.vetnova.inventario.model.Producto;
import com.vetnova.inventario.repository.MovimientoInventarioRepository;
import com.vetnova.inventario.repository.ProductoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovimientoInventarioService {

    private static final Logger logger =
            LoggerFactory.getLogger(MovimientoInventarioService.class);

    private final MovimientoInventarioRepository movimientoRepository;
    private final ProductoRepository productoRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final SucursalClient sucursalClient;

    public MovimientoInventarioService(
            MovimientoInventarioRepository movimientoRepository,
            ProductoRepository productoRepository,
            ApplicationEventPublisher eventPublisher,
            SucursalClient sucursalClient
    ) {
        this.movimientoRepository = movimientoRepository;
        this.productoRepository = productoRepository;
        this.eventPublisher = eventPublisher;
        this.sucursalClient = sucursalClient;
    }

    public List<MovimientoInventario> listarMovimientos() {
        return movimientoRepository.findAll();
    }

    public MovimientoInventario buscarPorId(Long id) {
        return movimientoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movimiento no encontrado con ID: " + id));
    }

    public MovimientoInventario registrarMovimiento(MovimientoInventario movimiento) {

        logger.info("Registrando movimiento. Producto ID: {}, Sucursal ID: {}, Tipo: {}, Cantidad: {}",
                movimiento.getIdProducto(),
                movimiento.getIdSucursal(),
                movimiento.getTipoMovimiento(),
                movimiento.getCantidad());

        sucursalClient.validarSucursal(movimiento.getIdSucursal());

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
                throw new RuntimeException("Tipo de movimiento inválido. Use ENTRADA, SALIDA o AJUSTE");
        }

        productoRepository.save(producto);

        MovimientoInventario movimientoGuardado = movimientoRepository.save(movimiento);

        logger.info("Movimiento registrado. Producto: {}, Stock actual: {}",
                producto.getNombre(),
                producto.getStockActual());

        if (producto.getStockActual() <= producto.getStockMinimo()) {
            publicarEventoStockBajo(producto, movimiento.getIdSucursal());
        }

        return movimientoGuardado;
    }

    private void publicarEventoStockBajo(Producto producto, Long idSucursal) {
        try {
            logger.warn("Stock bajo detectado. Producto: {}, Stock actual: {}, Stock mínimo: {}",
                    producto.getNombre(),
                    producto.getStockActual(),
                    producto.getStockMinimo());

            StockBajoEvent evento = new StockBajoEvent(
                    this,
                    producto.getNombre(),
                    producto.getStockActual(),
                    producto.getStockMinimo(),
                    idSucursal
            );

            eventPublisher.publishEvent(evento);

        } catch (Exception e) {
            logger.error("Error al publicar evento de stock bajo: {}", e.getMessage());
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