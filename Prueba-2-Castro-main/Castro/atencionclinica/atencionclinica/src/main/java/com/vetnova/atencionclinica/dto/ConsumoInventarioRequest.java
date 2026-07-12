package com.vetnova.atencionclinica.dto;

public class ConsumoInventarioRequest {

    private Long idProducto;
    private Long idSucursal;
    private Integer cantidad;
    private String origen;
    private Long idReferencia;
    private String observacion;

    public ConsumoInventarioRequest() {
    }

    public ConsumoInventarioRequest(Long idProducto, Long idSucursal, Integer cantidad, String origen, Long idReferencia, String observacion) {
        this.idProducto = idProducto;
        this.idSucursal = idSucursal;
        this.cantidad = cantidad;
        this.origen = origen;
        this.idReferencia = idReferencia;
        this.observacion = observacion;
    }

    public Long getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Long idProducto) {
        this.idProducto = idProducto;
    }

    public Long getIdSucursal() {
        return idSucursal;
    }

    public void setIdSucursal(Long idSucursal) {
        this.idSucursal = idSucursal;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public Long getIdReferencia() {
        return idReferencia;
    }

    public void setIdReferencia(Long idReferencia) {
        this.idReferencia = idReferencia;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }
}
