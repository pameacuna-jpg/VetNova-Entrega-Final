package com.vetnova.agenda.event;

import java.time.LocalDateTime;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

@Getter
public class CitaAgendadaEvent extends ApplicationEvent {
    
    private final Long idCita;
    private final Long idCliente;
    private final Long idMascota;
    private final LocalDateTime fechaHora;

    public CitaAgendadaEvent(Object source, Long idCita, Long idCliente, Long idMascota, LocalDateTime fechaHora) {
        super(source);
        this.idCita = idCita;
        this.idCliente = idCliente;
        this.idMascota = idMascota;
        this.fechaHora = fechaHora;
    }
}