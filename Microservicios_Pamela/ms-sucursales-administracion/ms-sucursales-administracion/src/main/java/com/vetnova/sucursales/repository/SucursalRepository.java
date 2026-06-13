package com.vetnova.sucursales.repository;

import com.vetnova.sucursales.model.Sucursal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SucursalRepository extends JpaRepository<Sucursal, Long> {

    List<Sucursal> findByEstadoIgnoreCase(String estado);

    List<Sucursal> findByCiudadIgnoreCase(String ciudad);
}