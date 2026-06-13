package com.vetnova.sucursales.controller;

import com.vetnova.sucursales.model.HorarioSucursal;
import com.vetnova.sucursales.service.HorarioSucursalService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.List;

@RestController
@RequestMapping("/api/v1/horarios")
public class HorarioSucursalController {

    @Autowired
    private HorarioSucursalService horarioSucursalService;

    @GetMapping
    public List<HorarioSucursal> listarHorarios() {

        return horarioSucursalService.listarHorarios();
    }

    @GetMapping("/activos")
    public List<HorarioSucursal> listarActivos() {

        return horarioSucursalService.listarActivos();
    }

    @GetMapping("/{id}")
    public HorarioSucursal buscarPorId(@PathVariable Long id) {

        return horarioSucursalService.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<HorarioSucursal> crearHorario(@Valid @RequestBody HorarioSucursal horario) {
        return ResponseEntity.ok(horarioSucursalService.crearHorario(horario));
    }


    @PutMapping("/{id}")
    public HorarioSucursal actualizarHorario(@PathVariable Long id,
                                             @Valid @RequestBody HorarioSucursal horario) {

        return horarioSucursalService.actualizarHorario(id, horario);
    }

    @DeleteMapping("/{id}")
    public String desactivarHorario(@PathVariable Long id) {

        horarioSucursalService.desactivarHorario(id);

        return "Horario desactivado correctamente";
    }

    @GetMapping("/sucursal/{idSucursal}")
    public List<HorarioSucursal> buscarPorSucursal(@PathVariable Long idSucursal) {

        return horarioSucursalService.buscarPorSucursal(idSucursal);
    }
}