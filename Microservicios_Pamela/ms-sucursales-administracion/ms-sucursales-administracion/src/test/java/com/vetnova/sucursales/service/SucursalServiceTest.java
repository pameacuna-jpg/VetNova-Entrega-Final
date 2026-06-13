package com.vetnova.sucursales.service;

import com.vetnova.sucursales.model.Sucursal;
import com.vetnova.sucursales.repository.SucursalRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SucursalServiceTest {

    @Mock
    private SucursalRepository sucursalRepository;

    @InjectMocks
    private SucursalService sucursalService;

    private Sucursal sucursal;

    @BeforeEach
    void setUp() {

        sucursal = new Sucursal(
                1L,
                "VetNova Concepción",
                "Av. O'Higgins 123",
                "+56412223344",
                "Concepción",
                "ACTIVA"
        );
    }

    @Test
    void listarSucursales_deberiaRetornarLista() {

        when(sucursalRepository.findAll())
                .thenReturn(Arrays.asList(sucursal));

        List<Sucursal> resultado =
                sucursalService.listarSucursales();

        assertEquals(1, resultado.size());

        verify(sucursalRepository).findAll();
    }

    @Test
    void listarSucursalesActivas_deberiaRetornarLista() {

        when(sucursalRepository.findByEstadoIgnoreCase("ACTIVA"))
                .thenReturn(Arrays.asList(sucursal));

        List<Sucursal> resultado =
                sucursalService.listarSucursalesActivas();

        assertEquals(1, resultado.size());

        verify(sucursalRepository)
                .findByEstadoIgnoreCase("ACTIVA");
    }

    @Test
    void buscarPorId_deberiaRetornarSucursal() {

        when(sucursalRepository.findById(1L))
                .thenReturn(Optional.of(sucursal));

        Sucursal resultado =
                sucursalService.buscarPorId(1L);

        assertEquals("VetNova Concepción",
                resultado.getNombre());

        verify(sucursalRepository).findById(1L);
    }

    @Test
    void crearSucursal_deberiaGuardarSucursal() {

        when(sucursalRepository.save(any(Sucursal.class)))
                .thenReturn(sucursal);

        Sucursal resultado =
                sucursalService.crearSucursal(sucursal);

        assertNotNull(resultado);
        assertEquals("ACTIVA",
                resultado.getEstado());

        verify(sucursalRepository).save(sucursal);
    }

    @Test
    void actualizarSucursal_deberiaModificarDatos() {

        Sucursal nueva = new Sucursal(
                null,
                "VetNova Chillán",
                "Av. Libertad 500",
                "+56425556666",
                "Chillán",
                "ACTIVA"
        );

        when(sucursalRepository.findById(1L))
                .thenReturn(Optional.of(sucursal));

        when(sucursalRepository.save(any(Sucursal.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Sucursal resultado =
                sucursalService.actualizarSucursal(1L, nueva);

        assertEquals("VetNova Chillán",
                resultado.getNombre());

        assertEquals("Chillán",
                resultado.getCiudad());

        verify(sucursalRepository).save(sucursal);
    }

    @Test
    void desactivarSucursal_deberiaCambiarEstado() {

        when(sucursalRepository.findById(1L))
                .thenReturn(Optional.of(sucursal));

        when(sucursalRepository.save(any(Sucursal.class)))
                .thenReturn(sucursal);

        sucursalService.desactivarSucursal(1L);

        assertEquals("INACTIVA",
                sucursal.getEstado());

        verify(sucursalRepository).save(sucursal);
    }

    @Test
    void buscarPorCiudad_deberiaRetornarResultados() {

        when(sucursalRepository.findByCiudadIgnoreCase("Concepción"))
                .thenReturn(Arrays.asList(sucursal));

        List<Sucursal> resultado =
                sucursalService.buscarPorCiudad("Concepción");

        assertEquals(1, resultado.size());

        verify(sucursalRepository)
                .findByCiudadIgnoreCase("Concepción");
    }
}
