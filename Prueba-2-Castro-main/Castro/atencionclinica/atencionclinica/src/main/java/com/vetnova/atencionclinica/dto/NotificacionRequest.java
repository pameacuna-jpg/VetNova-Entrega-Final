package com.vetnova.atencionclinica.dto;

public class NotificacionRequest {

    private String destino;
    private Long idCliente;
    private String mensaje;
    private String tipo;
    private String canal;
    private String prioridad;

    public NotificacionRequest() {
    }

    public NotificacionRequest(String destino, Long idCliente, String mensaje, String tipo, String canal, String prioridad) {
        this.destino = destino;
        this.idCliente = idCliente;
        this.mensaje = mensaje;
        this.tipo = tipo;
        this.canal = canal;
        this.prioridad = prioridad;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public Long getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Long idCliente) {
        this.idCliente = idCliente;
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
