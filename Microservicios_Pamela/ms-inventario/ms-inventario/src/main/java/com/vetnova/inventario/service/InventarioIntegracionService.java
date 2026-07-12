package com.vetnova.inventario.service;

import com.vetnova.inventario.client.SucursalClient;
import com.vetnova.inventario.dto.ConsumoInventarioRequestDTO;
import com.vetnova.inventario.dto.ConsumoInventarioResponseDTO;
import com.vetnova.inventario.dto.DisponibilidadProductoResponseDTO;
import com.vetnova.inventario.model.MovimientoInventario;
import com.vetnova.inventario.model.Producto;
import com.vetnova.inventario.repository.MovimientoInventarioRepository;
import com.vetnova.inventario.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventarioIntegracionService {

    private final ProductoRepository productoRepository;
    private final MovimientoInventarioRepository movimientoRepository;
    private final SucursalClient sucursalClient;
    private final MovimientoInventarioService movimientoInventarioService;

    public InventarioIntegracionService(
            ProductoRepository productoRepository,
            MovimientoInventarioRepository movimientoRepository,
            SucursalClient sucursalClient,
            MovimientoInventarioService movimientoInventarioService
    ) {
        this.productoRepository = productoRepository;
        this.movimientoRepository = movimientoRepository;
        this.sucursalClient = sucursalClient;
        this.movimientoInventarioService = movimientoInventarioService;
    }

    public DisponibilidadProductoResponseDTO consultarDisponibilidad(
            Long idProducto,
            Integer cantidad
    ) {
        if (cantidad == null || cantidad <= 0) {
            throw new RuntimeException("La cantidad solicitada debe ser mayor a cero");
        }

        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new RuntimeException(
                        "Producto no encontrado con ID: " + idProducto
                ));

        boolean activo = Boolean.TRUE.equals(producto.getActivo());
        boolean disponible = activo && producto.getStockActual() >= cantidad;

        String mensaje;

        if (!activo) {
            mensaje = "El producto se encuentra inactivo";
        } else if (!disponible) {
            mensaje = "Stock insuficiente";
        } else {
            mensaje = "Producto disponible";
        }

        return DisponibilidadProductoResponseDTO.builder()
                .idProducto(producto.getIdProducto())
                .nombreProducto(producto.getNombre())
                .stockActual(producto.getStockActual())
                .cantidadSolicitada(cantidad)
                .disponible(disponible)
                .activo(activo)
                .mensaje(mensaje)
                .build();
    }

    @Transactional
    public ConsumoInventarioResponseDTO registrarConsumo(
            ConsumoInventarioRequestDTO request
    ) {
        sucursalClient.validarSucursal(request.getIdSucursal());

        Producto producto = productoRepository.findById(request.getIdProducto())
                .orElseThrow(() -> new RuntimeException(
                        "Producto no encontrado con ID: " + request.getIdProducto()
                ));

        if (!Boolean.TRUE.equals(producto.getActivo())) {
            throw new RuntimeException("El producto se encuentra inactivo");
        }

        if (producto.getStockActual() < request.getCantidad()) {
            throw new RuntimeException("Stock insuficiente para realizar el consumo");
        }

        int stockAnterior = producto.getStockActual();

        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setIdProducto(request.getIdProducto());
        movimiento.setIdSucursal(request.getIdSucursal());
        movimiento.setCantidad(request.getCantidad());
        movimiento.setTipoMovimiento("SALIDA");

        MovimientoInventario movimientoGuardado =
                movimientoInventarioService.registrarMovimiento(movimiento);

        Producto productoActualizado = productoRepository.findById(request.getIdProducto())
                .orElseThrow(() -> new RuntimeException(
                        "Producto no encontrado después de actualizar inventario"
                ));

        return ConsumoInventarioResponseDTO.builder()
                .idMovimiento(movimientoGuardado.getIdMovimiento())
                .idProducto(request.getIdProducto())
                .idSucursal(request.getIdSucursal())
                .cantidadConsumida(request.getCantidad())
                .stockAnterior(stockAnterior)
                .stockActual(productoActualizado.getStockActual())
                .origen(request.getOrigen())
                .idReferencia(request.getIdReferencia())
                .mensaje(
                        "Consumo registrado correctamente desde "
                                + request.getOrigen()
                )
                .build();
    }
}