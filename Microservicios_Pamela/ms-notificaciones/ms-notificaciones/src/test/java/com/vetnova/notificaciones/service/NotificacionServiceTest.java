package com.vetnova.notificaciones.service;

import com.vetnova.notificaciones.client.ClienteClient;
import com.vetnova.notificaciones.dto.ClienteResponseDTO;
import com.vetnova.notificaciones.dto.NotificacionRequestDTO;
import com.vetnova.notificaciones.dto.NotificacionResponseDTO;
import com.vetnova.notificaciones.enums.CanalNotificacion;
import com.vetnova.notificaciones.enums.DestinoNotificacion;
import com.vetnova.notificaciones.enums.EstadoNotificacion;
import com.vetnova.notificaciones.enums.PrioridadNotificacion;
import com.vetnova.notificaciones.enums.TipoNotificacion;
import com.vetnova.notificaciones.exception.NegocioException;
import com.vetnova.notificaciones.exception.RecursoNoEncontradoException;
import com.vetnova.notificaciones.model.Notificacion;
import com.vetnova.notificaciones.repository.NotificacionRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificacionServiceTest {

    @Mock
    private NotificacionRepository notificacionRepository;

    @Mock
    private ClienteClient clienteClient;

    @InjectMocks
    private NotificacionService notificacionService;

    private ClienteResponseDTO cliente;
    private NotificacionRequestDTO request;
    private Notificacion notificacion;

    @BeforeEach
    void setUp() {

        cliente = new ClienteResponseDTO();
        cliente.setIdCliente(1L);
        cliente.setNombre("Juan Pérez");
        cliente.setEmail("juan@correo.cl");
        cliente.setTelefono("999999999");
        cliente.setEstado("ACTIVO");

        request = new NotificacionRequestDTO();
        request.setDestino(DestinoNotificacion.CLIENTE);
        request.setIdCliente(1L);
        request.setDestinatario(null);
        request.setMensaje(" Mensaje de prueba ");
        request.setTipo(TipoNotificacion.STOCK_BAJO);
        request.setCanal(CanalNotificacion.EMAIL);
        request.setPrioridad(PrioridadNotificacion.ALTA);

        notificacion = Notificacion.builder()
                .idNotificacion(1L)
                .destino(DestinoNotificacion.CLIENTE)
                .idCliente(1L)
                .destinatario("juan@correo.cl")
                .mensaje("Mensaje de prueba")
                .tipo(TipoNotificacion.STOCK_BAJO)
                .canal(CanalNotificacion.EMAIL)
                .prioridad(PrioridadNotificacion.ALTA)
                .estado(EstadoNotificacion.PENDIENTE)
                .build();
    }

    @Test
    void crear_conCanalEmail_deberiaCrearNotificacionPendiente() {

        when(clienteClient.obtenerClientePorId(1L))
                .thenReturn(cliente);

        when(notificacionRepository.save(any(Notificacion.class)))
                .thenReturn(notificacion);

        NotificacionResponseDTO resultado =
                notificacionService.crear(request);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdNotificacion());
        assertEquals(DestinoNotificacion.CLIENTE, resultado.getDestino());
        assertEquals(1L, resultado.getIdCliente());
        assertEquals("juan@correo.cl", resultado.getDestinatario());
        assertEquals("Mensaje de prueba", resultado.getMensaje());
        assertEquals(TipoNotificacion.STOCK_BAJO, resultado.getTipo());
        assertEquals(CanalNotificacion.EMAIL, resultado.getCanal());
        assertEquals(PrioridadNotificacion.ALTA, resultado.getPrioridad());
        assertEquals(EstadoNotificacion.PENDIENTE, resultado.getEstado());

        verify(clienteClient).obtenerClientePorId(1L);
        verify(notificacionRepository).save(any(Notificacion.class));
    }

    @Test
    void crear_conCanalSms_deberiaUsarTelefonoComoDestinatario() {

        request.setCanal(CanalNotificacion.SMS);

        Notificacion notificacionSms = Notificacion.builder()
                .idNotificacion(2L)
                .destino(DestinoNotificacion.CLIENTE)
                .idCliente(1L)
                .destinatario("999999999")
                .mensaje("Mensaje de prueba")
                .tipo(TipoNotificacion.STOCK_BAJO)
                .canal(CanalNotificacion.SMS)
                .prioridad(PrioridadNotificacion.ALTA)
                .estado(EstadoNotificacion.PENDIENTE)
                .build();

        when(clienteClient.obtenerClientePorId(1L))
                .thenReturn(cliente);

        when(notificacionRepository.save(any(Notificacion.class)))
                .thenReturn(notificacionSms);

        NotificacionResponseDTO resultado =
                notificacionService.crear(request);

        assertNotNull(resultado);
        assertEquals(DestinoNotificacion.CLIENTE, resultado.getDestino());
        assertEquals("999999999", resultado.getDestinatario());
        assertEquals(CanalNotificacion.SMS, resultado.getCanal());

        verify(clienteClient).obtenerClientePorId(1L);
        verify(notificacionRepository).save(any(Notificacion.class));
    }

    @Test
    void crear_conCanalWhatsapp_deberiaUsarTelefonoComoDestinatario() {

        request.setCanal(CanalNotificacion.WHATSAPP);

        Notificacion notificacionWhatsapp = Notificacion.builder()
                .idNotificacion(3L)
                .destino(DestinoNotificacion.CLIENTE)
                .idCliente(1L)
                .destinatario("999999999")
                .mensaje("Mensaje de prueba")
                .tipo(TipoNotificacion.STOCK_BAJO)
                .canal(CanalNotificacion.WHATSAPP)
                .prioridad(PrioridadNotificacion.ALTA)
                .estado(EstadoNotificacion.PENDIENTE)
                .build();

        when(clienteClient.obtenerClientePorId(1L))
                .thenReturn(cliente);

        when(notificacionRepository.save(any(Notificacion.class)))
                .thenReturn(notificacionWhatsapp);

        NotificacionResponseDTO resultado =
                notificacionService.crear(request);

        assertNotNull(resultado);
        assertEquals(DestinoNotificacion.CLIENTE, resultado.getDestino());
        assertEquals("999999999", resultado.getDestinatario());
        assertEquals(CanalNotificacion.WHATSAPP, resultado.getCanal());

        verify(clienteClient).obtenerClientePorId(1L);
        verify(notificacionRepository).save(any(Notificacion.class));
    }

    @Test
    void crear_cuandoClienteEsNull_deberiaLanzarNegocioException() {

        when(clienteClient.obtenerClientePorId(1L))
                .thenReturn(null);

        NegocioException exception = assertThrows(
                NegocioException.class,
                () -> notificacionService.crear(request)
        );

        assertEquals(
                "No fue posible obtener los datos del cliente",
                exception.getMessage()
        );

        verify(clienteClient).obtenerClientePorId(1L);
        verify(notificacionRepository, never())
                .save(any(Notificacion.class));
    }

    @Test
    void crear_cuandoClienteNoTieneId_deberiaLanzarNegocioException() {

        cliente.setIdCliente(null);

        when(clienteClient.obtenerClientePorId(1L))
                .thenReturn(cliente);

        NegocioException exception = assertThrows(
                NegocioException.class,
                () -> notificacionService.crear(request)
        );

        assertEquals(
                "No fue posible obtener los datos del cliente",
                exception.getMessage()
        );

        verify(clienteClient).obtenerClientePorId(1L);
        verify(notificacionRepository, never())
                .save(any(Notificacion.class));
    }

    @Test
    void crear_cuandoClienteInactivo_deberiaLanzarNegocioException() {

        cliente.setEstado("INACTIVO");

        when(clienteClient.obtenerClientePorId(1L))
                .thenReturn(cliente);

        NegocioException exception = assertThrows(
                NegocioException.class,
                () -> notificacionService.crear(request)
        );

        assertEquals(
                "El cliente no se encuentra activo",
                exception.getMessage()
        );

        verify(clienteClient).obtenerClientePorId(1L);
        verify(notificacionRepository, never())
                .save(any(Notificacion.class));
    }

    @Test
    void crear_emailSinCorreo_deberiaLanzarNegocioException() {

        cliente.setEmail("");

        when(clienteClient.obtenerClientePorId(1L))
                .thenReturn(cliente);

        NegocioException exception = assertThrows(
                NegocioException.class,
                () -> notificacionService.crear(request)
        );

        assertEquals(
                "El cliente no tiene email registrado",
                exception.getMessage()
        );

        verify(clienteClient).obtenerClientePorId(1L);
        verify(notificacionRepository, never())
                .save(any(Notificacion.class));
    }

    @Test
    void crear_smsSinTelefono_deberiaLanzarNegocioException() {

        request.setCanal(CanalNotificacion.SMS);
        cliente.setTelefono("");

        when(clienteClient.obtenerClientePorId(1L))
                .thenReturn(cliente);

        NegocioException exception = assertThrows(
                NegocioException.class,
                () -> notificacionService.crear(request)
        );

        assertEquals(
                "El cliente no tiene teléfono registrado",
                exception.getMessage()
        );

        verify(clienteClient).obtenerClientePorId(1L);
        verify(notificacionRepository, never())
                .save(any(Notificacion.class));
    }

    @Test
    void crear_internaConEmailValido_deberiaCrearNotificacion() {

        NotificacionRequestDTO requestInterna = new NotificacionRequestDTO();
        requestInterna.setDestino(DestinoNotificacion.INTERNA);
        requestInterna.setDestinatario("inventario@vetnova.cl");
        requestInterna.setMensaje("Alerta interna por stock bajo");
        requestInterna.setTipo(TipoNotificacion.STOCK_BAJO);
        requestInterna.setCanal(CanalNotificacion.EMAIL);
        requestInterna.setPrioridad(PrioridadNotificacion.ALTA);

        Notificacion notificacionInterna = Notificacion.builder()
                .idNotificacion(4L)
                .destino(DestinoNotificacion.INTERNA)
                .idCliente(null)
                .destinatario("inventario@vetnova.cl")
                .mensaje("Alerta interna por stock bajo")
                .tipo(TipoNotificacion.STOCK_BAJO)
                .canal(CanalNotificacion.EMAIL)
                .prioridad(PrioridadNotificacion.ALTA)
                .estado(EstadoNotificacion.PENDIENTE)
                .build();

        when(notificacionRepository.save(any(Notificacion.class)))
                .thenReturn(notificacionInterna);

        NotificacionResponseDTO resultado =
                notificacionService.crear(requestInterna);

        assertNotNull(resultado);
        assertEquals(DestinoNotificacion.INTERNA, resultado.getDestino());
        assertNull(resultado.getIdCliente());
        assertEquals(
                "inventario@vetnova.cl",
                resultado.getDestinatario()
        );

        verify(clienteClient, never())
                .obtenerClientePorId(anyLong());

        verify(notificacionRepository)
                .save(any(Notificacion.class));
    }

    @Test
    void crear_internaSinDestinatario_deberiaLanzarNegocioException() {

        NotificacionRequestDTO requestInterna = new NotificacionRequestDTO();
        requestInterna.setDestino(DestinoNotificacion.INTERNA);
        requestInterna.setDestinatario("");
        requestInterna.setMensaje("Alerta interna por stock bajo");
        requestInterna.setTipo(TipoNotificacion.STOCK_BAJO);
        requestInterna.setCanal(CanalNotificacion.EMAIL);
        requestInterna.setPrioridad(PrioridadNotificacion.ALTA);

        NegocioException exception = assertThrows(
                NegocioException.class,
                () -> notificacionService.crear(requestInterna)
        );

        assertEquals(
                "Para notificaciones INTERNAS, el destinatario es obligatorio",
                exception.getMessage()
        );

        verify(clienteClient, never())
                .obtenerClientePorId(anyLong());

        verify(notificacionRepository, never())
                .save(any(Notificacion.class));
    }

    @Test
    void crear_internaConEmailInvalido_deberiaLanzarNegocioException() {

        NotificacionRequestDTO requestInterna = new NotificacionRequestDTO();
        requestInterna.setDestino(DestinoNotificacion.INTERNA);
        requestInterna.setDestinatario("correo-invalido");
        requestInterna.setMensaje("Alerta interna por stock bajo");
        requestInterna.setTipo(TipoNotificacion.STOCK_BAJO);
        requestInterna.setCanal(CanalNotificacion.EMAIL);
        requestInterna.setPrioridad(PrioridadNotificacion.ALTA);

        NegocioException exception = assertThrows(
                NegocioException.class,
                () -> notificacionService.crear(requestInterna)
        );

        assertEquals(
                "El destinatario interno debe ser un email válido",
                exception.getMessage()
        );

        verify(notificacionRepository, never())
                .save(any(Notificacion.class));
    }

    @Test
    void listar_deberiaRetornarTodasLasNotificaciones() {

        when(notificacionRepository.findAll())
                .thenReturn(Arrays.asList(notificacion));

        List<NotificacionResponseDTO> resultado =
                notificacionService.listar();

        assertEquals(1, resultado.size());
        assertEquals(
                1L,
                resultado.get(0).getIdNotificacion()
        );

        verify(notificacionRepository).findAll();
    }

    @Test
    void buscarPorId_cuandoExiste_deberiaRetornarNotificacion() {

        when(notificacionRepository.findById(1L))
                .thenReturn(Optional.of(notificacion));

        NotificacionResponseDTO resultado =
                notificacionService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdNotificacion());
        assertEquals(
                "Mensaje de prueba",
                resultado.getMensaje()
        );

        verify(notificacionRepository).findById(1L);
    }

    @Test
    void buscarPorId_cuandoNoExiste_deberiaLanzarRecursoNoEncontradoException() {

        when(notificacionRepository.findById(99L))
                .thenReturn(Optional.empty());

        RecursoNoEncontradoException exception = assertThrows(
                RecursoNoEncontradoException.class,
                () -> notificacionService.buscarPorId(99L)
        );

        assertEquals(
                "No existe una notificación con id 99",
                exception.getMessage()
        );

        verify(notificacionRepository).findById(99L);
    }

    @Test
    void buscarPorCliente_deberiaRetornarNotificacionesDelCliente() {

        when(clienteClient.obtenerClientePorId(1L))
                .thenReturn(cliente);

        when(notificacionRepository.findByIdCliente(1L))
                .thenReturn(Arrays.asList(notificacion));

        List<NotificacionResponseDTO> resultado =
                notificacionService.buscarPorCliente(1L);

        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getIdCliente());

        verify(clienteClient).obtenerClientePorId(1L);
        verify(notificacionRepository).findByIdCliente(1L);
    }

    @Test
    void buscarPorEstado_deberiaRetornarNotificacionesPorEstado() {

        when(notificacionRepository.findByEstado(
                EstadoNotificacion.PENDIENTE
        )).thenReturn(Arrays.asList(notificacion));

        List<NotificacionResponseDTO> resultado =
                notificacionService.buscarPorEstado(
                        EstadoNotificacion.PENDIENTE
                );

        assertEquals(1, resultado.size());
        assertEquals(
                EstadoNotificacion.PENDIENTE,
                resultado.get(0).getEstado()
        );

        verify(notificacionRepository)
                .findByEstado(EstadoNotificacion.PENDIENTE);
    }

    @Test
    void buscarPorTipo_deberiaRetornarNotificacionesPorTipo() {

        when(notificacionRepository.findByTipo(
                TipoNotificacion.STOCK_BAJO
        )).thenReturn(Arrays.asList(notificacion));

        List<NotificacionResponseDTO> resultado =
                notificacionService.buscarPorTipo(
                        TipoNotificacion.STOCK_BAJO
                );

        assertEquals(1, resultado.size());
        assertEquals(
                TipoNotificacion.STOCK_BAJO,
                resultado.get(0).getTipo()
        );

        verify(notificacionRepository)
                .findByTipo(TipoNotificacion.STOCK_BAJO);
    }

    @Test
    void marcarComoEnviada_cuandoPendiente_deberiaActualizarEstado() {

        when(notificacionRepository.findById(1L))
                .thenReturn(Optional.of(notificacion));

        Notificacion notificacionEnviada = Notificacion.builder()
                .idNotificacion(1L)
                .destino(DestinoNotificacion.CLIENTE)
                .idCliente(1L)
                .destinatario("juan@correo.cl")
                .mensaje("Mensaje de prueba")
                .tipo(TipoNotificacion.STOCK_BAJO)
                .canal(CanalNotificacion.EMAIL)
                .prioridad(PrioridadNotificacion.ALTA)
                .estado(EstadoNotificacion.ENVIADA)
                .build();

        when(notificacionRepository.save(any(Notificacion.class)))
                .thenReturn(notificacionEnviada);

        NotificacionResponseDTO resultado =
                notificacionService.marcarComoEnviada(1L);

        assertEquals(
                EstadoNotificacion.ENVIADA,
                resultado.getEstado()
        );

        verify(notificacionRepository).findById(1L);
        verify(notificacionRepository)
                .save(any(Notificacion.class));
    }

    @Test
    void marcarComoEnviada_cuandoYaEstaEnviada_deberiaLanzarNegocioException() {

        notificacion.setEstado(EstadoNotificacion.ENVIADA);

        when(notificacionRepository.findById(1L))
                .thenReturn(Optional.of(notificacion));

        NegocioException exception = assertThrows(
                NegocioException.class,
                () -> notificacionService.marcarComoEnviada(1L)
        );

        assertEquals(
                "La notificación ya se encuentra marcada como ENVIADA",
                exception.getMessage()
        );

        verify(notificacionRepository, never())
                .save(any(Notificacion.class));
    }

    @Test
    void marcarComoError_cuandoPendiente_deberiaActualizarEstado() {

        when(notificacionRepository.findById(1L))
                .thenReturn(Optional.of(notificacion));

        Notificacion notificacionError = Notificacion.builder()
                .idNotificacion(1L)
                .destino(DestinoNotificacion.CLIENTE)
                .idCliente(1L)
                .destinatario("juan@correo.cl")
                .mensaje("Mensaje de prueba")
                .tipo(TipoNotificacion.STOCK_BAJO)
                .canal(CanalNotificacion.EMAIL)
                .prioridad(PrioridadNotificacion.ALTA)
                .estado(EstadoNotificacion.ERROR)
                .build();

        when(notificacionRepository.save(any(Notificacion.class)))
                .thenReturn(notificacionError);

        NotificacionResponseDTO resultado =
                notificacionService.marcarComoError(1L);

        assertEquals(
                EstadoNotificacion.ERROR,
                resultado.getEstado()
        );

        verify(notificacionRepository).findById(1L);
        verify(notificacionRepository)
                .save(any(Notificacion.class));
    }

    @Test
    void marcarComoError_cuandoYaEstaError_deberiaLanzarNegocioException() {

        notificacion.setEstado(EstadoNotificacion.ERROR);

        when(notificacionRepository.findById(1L))
                .thenReturn(Optional.of(notificacion));

        NegocioException exception = assertThrows(
                NegocioException.class,
                () -> notificacionService.marcarComoError(1L)
        );

        assertEquals(
                "La notificación ya se encuentra marcada como ERROR",
                exception.getMessage()
        );

        verify(notificacionRepository, never())
                .save(any(Notificacion.class));
    }

    @Test
    void eliminar_cuandoExiste_deberiaEliminarNotificacion() {

        when(notificacionRepository.findById(1L))
                .thenReturn(Optional.of(notificacion));

        notificacionService.eliminar(1L);

        verify(notificacionRepository).findById(1L);
        verify(notificacionRepository).delete(notificacion);
    }

    @Test
    void eliminar_cuandoNoExiste_deberiaLanzarRecursoNoEncontradoException() {

        when(notificacionRepository.findById(99L))
                .thenReturn(Optional.empty());

        RecursoNoEncontradoException exception = assertThrows(
                RecursoNoEncontradoException.class,
                () -> notificacionService.eliminar(99L)
        );

        assertEquals(
                "No existe una notificación con id 99",
                exception.getMessage()
        );

        verify(notificacionRepository, never())
                .delete(any(Notificacion.class));
    }
}