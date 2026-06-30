package com.vetnova.atencionclinica.dto;

public class NotificacionRequest {

    private String destinatario;
    private String mensaje;
    private String tipo;
    private String canal;
    private String prioridad;

    public NotificacionRequest() {
    }

    public NotificacionRequest(String destinatario, String mensaje, String tipo, String canal, String prioridad) {
        this.destinatario = destinatario;
        this.mensaje = mensaje;
        this.tipo = tipo;
        this.canal = canal;
        this.prioridad = prioridad;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getCanal() {
        return canal;
    }

    public void setCanal(String canal) {
        this.canal = canal;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }
}
