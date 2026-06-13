package com.vetnova.sucursales.repository;

import com.vetnova.sucursales.model.BoxAtencion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoxAtencionRepository extends JpaRepository<BoxAtencion, Long> {

    List<BoxAtencion> findByIdSucursal(Long idSucursal);

    List<BoxAtencion> findByDisponibleTrue();
}
