package com.vetnova.inventario.repository;

import com.vetnova.inventario.model.MovimientoInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {

    List<MovimientoInventario> findByIdProducto(Long idProducto);

    List<MovimientoInventario> findByIdSucursal(Long idSucursal);

    List<MovimientoInventario> findByTipoMovimientoIgnoreCase(String tipoMovimiento);
}
