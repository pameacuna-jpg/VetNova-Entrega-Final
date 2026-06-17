package com.vetnova.inventario.service;

import com.vetnova.inventario.model.Proveedor;
import com.vetnova.inventario.repository.ProveedorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProveedorService {

    
    private final ProveedorRepository proveedorRepository;

    public ProveedorService(ProveedorRepository proveedorRepository) {
        this.proveedorRepository = proveedorRepository;
    }

    public List<Proveedor> listarProveedores() {
        return proveedorRepository.findByActivoTrue();
    }

    public Proveedor buscarPorId(Long id) {
        return proveedorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado con ID: " + id));
    }

    public Proveedor crearProveedor(Proveedor proveedor) {
        proveedor.setActivo(true);
        return proveedorRepository.save(proveedor);
    }

    public Proveedor actualizarProveedor(Long id, Proveedor datos) {
        Proveedor proveedor = buscarPorId(id);

        proveedor.setNombre(datos.getNombre());
        proveedor.setTelefono(datos.getTelefono());
        proveedor.setEmail(datos.getEmail());
        proveedor.setDireccion(datos.getDireccion());

        return proveedorRepository.save(proveedor);
    }

    public void eliminarProveedor(Long id) {
        Proveedor proveedor = buscarPorId(id);
        proveedor.setActivo(false);
        proveedorRepository.save(proveedor);
    }

    public List<Proveedor> buscarPorNombre(String nombre) {
        return proveedorRepository.findByNombreContainingIgnoreCase(nombre);
    }
}
