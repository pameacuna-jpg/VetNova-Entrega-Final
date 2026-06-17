package com.vetnova.sucursales.repository;

import com.vetnova.sucursales.model.HorarioSucursal;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;


public interface HorarioSucursalRepository extends JpaRepository<HorarioSucursal, Long> {

    List<HorarioSucursal> findByIdSucursal(Long idSucursal);

    List<HorarioSucursal> findByActivoTrue();
}