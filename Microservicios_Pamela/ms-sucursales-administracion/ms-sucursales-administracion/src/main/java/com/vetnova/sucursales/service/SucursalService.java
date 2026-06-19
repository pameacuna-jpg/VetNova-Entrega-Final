package com.vetnova.sucursales.service;

import com.vetnova.sucursales.model.Sucursal;
import com.vetnova.sucursales.repository.SucursalRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SucursalService {


    private final SucursalRepository sucursalRepository;

    public SucursalService(SucursalRepository sucursalRepository) {
        this.sucursalRepository = sucursalRepository;
    }

    public List<Sucursal> listarSucursales() {
        return sucursalRepository.findAll();
    }

    public List<Sucursal> listarSucursalesActivas() {
        return sucursalRepository.findByEstadoIgnoreCase("ACTIVA");
    }

    public Sucursal buscarPorId(Long id) {
        return sucursalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sucursal no encontrada con ID: " + id));
    }

    public Sucursal crearSucursal(Sucursal sucursal) {
        sucursal.setEstado("ACTIVA");
        return sucursalRepository.save(sucursal);
    }

    public Sucursal actualizarSucursal(Long id, Sucursal datos) {
        Sucursal sucursal = buscarPorId(id);

        sucursal.setNombre(datos.getNombre());
        sucursal.setDireccion(datos.getDireccion());
        sucursal.setTelefono(datos.getTelefono());
        sucursal.setCiudad(datos.getCiudad());

        return sucursalRepository.save(sucursal);
    }

    public void desactivarSucursal(Long id) {
        Sucursal sucursal = buscarPorId(id);
        sucursal.setEstado("INACTIVA");
        sucursalRepository.save(sucursal);
    }

    public List<Sucursal> buscarPorCiudad(String ciudad) {
        return sucursalRepository.findByCiudadIgnoreCase(ciudad);
    }
}
