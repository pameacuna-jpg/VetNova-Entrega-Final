package com.vetnova.inventario.repository;

import com.vetnova.inventario.model.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {

    List<Proveedor> findByActivoTrue();

    List<Proveedor> findByNombreContainingIgnoreCase(String nombre);
}