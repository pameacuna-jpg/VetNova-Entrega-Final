package com.vetnova.sucursales.service;

import com.vetnova.sucursales.model.HorarioSucursal;
import com.vetnova.sucursales.repository.HorarioSucursalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HorarioSucursalService {

    @Autowired
    private HorarioSucursalRepository horarioSucursalRepository;

    public List<HorarioSucursal> listarHorarios() {

        return horarioSucursalRepository.findAll();
    }

    public List<HorarioSucursal> listarActivos() {

        return horarioSucursalRepository.findByActivoTrue();
    }

    public HorarioSucursal buscarPorId(Long id) {

        return horarioSucursalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Horario no encontrado con ID: " + id));
    }

    public HorarioSucursal crearHorario(HorarioSucursal horario) {

        horario.setActivo(true);

        return horarioSucursalRepository.save(horario);
    }

    public HorarioSucursal actualizarHorario(Long id,
                                             HorarioSucursal datos) {

        HorarioSucursal horario = buscarPorId(id);

        horario.setDia(datos.getDia());
        horario.setHoraApertura(datos.getHoraApertura());
        horario.setHoraCierre(datos.getHoraCierre());
        horario.setIdSucursal(datos.getIdSucursal());

        return horarioSucursalRepository.save(horario);
    }

    public void desactivarHorario(Long id) {

        HorarioSucursal horario = buscarPorId(id);

        horario.setActivo(false);

        horarioSucursalRepository.save(horario);
    }

    public List<HorarioSucursal> buscarPorSucursal(Long idSucursal) {

        return horarioSucursalRepository.findByIdSucursal(idSucursal);
    }
}