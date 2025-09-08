package com.cfo.reporting.service.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.concurrent.TimeUnit;

public class ConfigCache {
    private long tiempoExpiracion;
    private TimeUnit unidadTiempo;

    public ConfigCache() {}

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