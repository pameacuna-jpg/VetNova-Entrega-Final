package com.vetnova.sucursales.repository;

import com.vetnova.sucursales.model.HorarioSucursal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HorarioSucursalRepository extends JpaRepository<HorarioSucursal, Long> {

    List<HorarioSucursal> findByIdSucursal(Long idSucursal);

    List<HorarioSucursal> findByActivoTrue();
}