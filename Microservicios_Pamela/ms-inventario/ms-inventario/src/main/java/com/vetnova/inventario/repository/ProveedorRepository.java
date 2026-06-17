package com.vetnova.inventario.repository;

import com.vetnova.inventario.model.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;


public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {

    List<Proveedor> findByActivoTrue();

    List<Proveedor> findByNombreContainingIgnoreCase(String nombre);
}