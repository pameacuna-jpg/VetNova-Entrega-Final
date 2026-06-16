@Test
void buscarPorId_cuandoNoExiste_deberiaLanzarExcepcion() {

    when(notificacionRepository.findById(99L))
            .thenReturn(Optional.empty());

    RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> notificacionService.buscarPorId(99L)
    );

    assertEquals("Notificación no encontrada con ID: 99", exception.getMessage());

    verify(notificacionRepository).findById(99L);
}

@Test
void marcarEnviada_cuandoNoExiste_deberiaLanzarExcepcion() {

    when(notificacionRepository.findById(99L))
            .thenReturn(Optional.empty());

    RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> notificacionService.marcarEnviada(99L)
    );

    assertEquals("Notificación no encontrada con ID: 99", exception.getMessage());

    verify(notificacionRepository).findById(99L);
    verify(notificacionRepository, never()).save(any(Notificacion.class));
}