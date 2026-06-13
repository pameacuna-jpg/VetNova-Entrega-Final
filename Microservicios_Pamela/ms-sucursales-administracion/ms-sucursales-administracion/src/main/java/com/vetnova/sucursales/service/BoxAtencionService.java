package com.vetnova.sucursales.service;

import com.vetnova.sucursales.model.BoxAtencion;
import com.vetnova.sucursales.repository.BoxAtencionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoxAtencionService {

    @Autowired
    private BoxAtencionRepository boxAtencionRepository;

    public List<BoxAtencion> listarBoxes() {
        return boxAtencionRepository.findAll();
    }

    public List<BoxAtencion> listarDisponibles() {
        return boxAtencionRepository.findByDisponibleTrue();
    }

    public BoxAtencion buscarPorId(Long id) {
        return boxAtencionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Box no encontrado con ID: " + id));
    }

    public BoxAtencion crearBox(BoxAtencion box) {
        box.setDisponible(true);
        return boxAtencionRepository.save(box);
    }

    public BoxAtencion actualizarBox(Long id, BoxAtencion datos) {

        BoxAtencion box = buscarPorId(id);

        box.setNombre(datos.getNombre());
        box.setTipoAtencion(datos.getTipoAtencion());
        box.setIdSucursal(datos.getIdSucursal());

        return boxAtencionRepository.save(box);
    }

    public void desactivarBox(Long id) {

        BoxAtencion box = buscarPorId(id);

        box.setDisponible(false);

        boxAtencionRepository.save(box);
    }

    public List<BoxAtencion> buscarPorSucursal(Long idSucursal) {

        return boxAtencionRepository.findByIdSucursal(idSucursal);
    }
}