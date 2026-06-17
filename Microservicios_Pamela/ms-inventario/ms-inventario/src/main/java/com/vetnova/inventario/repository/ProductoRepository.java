package com.vetnova.inventario.repository;

import com.vetnova.inventario.model.Producto;

import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByActivoTrue();

    List<Producto> findByCategoriaIgnoreCase(String categoria);

    List<Producto> findByStockActualLessThanEqual(Integer stockMinimo);
   
    
}
