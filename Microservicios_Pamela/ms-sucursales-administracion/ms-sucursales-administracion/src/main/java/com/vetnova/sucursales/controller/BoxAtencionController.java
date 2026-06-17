package com.vetnova.sucursales.controller;

import com.vetnova.sucursales.model.BoxAtencion;
import com.vetnova.sucursales.service.BoxAtencionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.List;

@RestController
@RequestMapping("/api/v1/boxes")
public class BoxAtencionController {


    private final BoxAtencionService boxAtencionService;

    public BoxAtencionController(BoxAtencionService boxAtencionService) {
        this.boxAtencionService = boxAtencionService;
    }
    
    @GetMapping
    public List<BoxAtencion> listarBoxes() {

        return boxAtencionService.listarBoxes();
    }

    @GetMapping("/disponibles")
    public List<BoxAtencion> listarDisponibles() {

        return boxAtencionService.listarDisponibles();
    }

    @GetMapping("/{id}")
    public BoxAtencion buscarPorId(@PathVariable Long id) {

        return boxAtencionService.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<BoxAtencion> crearBox(@Valid @RequestBody BoxAtencion box) {
        return ResponseEntity.ok(boxAtencionService.crearBox(box));
   }


    @PutMapping("/{id}")
    public BoxAtencion actualizarBox(@PathVariable Long id,
                                     @Valid @RequestBody BoxAtencion box) {

        return boxAtencionService.actualizarBox(id, box);
    }

    @DeleteMapping("/{id}")
    public String desactivarBox(@PathVariable Long id) {

        boxAtencionService.desactivarBox(id);

        return "Box desactivado correctamente";
    }

    @GetMapping("/sucursal/{idSucursal}")
    public List<BoxAtencion> buscarPorSucursal(@PathVariable Long idSucursal) {

        return boxAtencionService.buscarPorSucursal(idSucursal);
    }
}