package com.cfo.reporting.service.config;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.concurrent.TimeUnit;

public class ScreenConfig {
    private String clavePantalla;
    private String nombreTabla;
    private String query;
    private String claveCache;
    private String valorCache;
    private long tiempoExpiracion;
    private TimeUnit unidadTiempo;

    // Constructor por defecto
    public ScreenConfig() {}

    // Getters y Setters
    public String getClavePantalla() {
        return clavePantalla;
    }

    public void setClavePantalla(String clavePantalla) {
        this.clavePantalla = clavePantalla;
    }

    public String getNombreTabla() {
        return nombreTabla;
    }

    public void setNombreTabla(String nombreTabla) {
        this.nombreTabla = nombreTabla;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getClaveCache() {
        return claveCache;
    }

    public void setClaveCache(String claveCache) {
        this.claveCache = claveCache;
    }

    public String getValorCache() {
        return valorCache;
    }

    public void setValorCache(String valorCache) {
        this.valorCache = valorCache;
    }

    public long getTiempoExpiracion() {
        return tiempoExpiracion;
    }

    public void setTiempoExpiracion(long tiempoExpiracion) {
        this.tiempoExpiracion = tiempoExpiracion;
    }

    public TimeUnit getUnidadTiempo() {
        return unidadTiempo;
    }

    @JsonProperty("unidadTiempo")
    public void setUnidadTiempo(String unidadTiempo) {
        this.unidadTiempo = TimeUnit.valueOf(unidadTiempo);
    }
}