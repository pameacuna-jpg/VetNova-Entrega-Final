package com.vetnova.ventas.dto;

public class MovimientoInventarioRequest {

    private Long idProducto;
    private Long idSucursal;
    private String tipoMovimiento;
    private Integer cantidad;
    private String observacion;

    public MovimientoInventarioRequest() {
    }

    public MovimientoInventarioRequest(Long idProducto, Long idSucursal, String tipoMovimiento, Integer cantidad, String observacion) {
        this.idProducto = idProducto;
        this.idSucursal = idSucursal;
        this.tipoMovimiento = tipoMovimiento;
        this.cantidad = cantidad;
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

    public String getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(String tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }
}