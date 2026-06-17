package com.vetnova.inventario.repository;

import com.vetnova.inventario.model.MovimientoInventario;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;


public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {

    List<MovimientoInventario> findByIdProducto(Long idProducto);

    List<MovimientoInventario> findByIdSucursal(Long idSucursal);

    List<MovimientoInventario> findByTipoMovimientoIgnoreCase(String tipoMovimiento);
}
