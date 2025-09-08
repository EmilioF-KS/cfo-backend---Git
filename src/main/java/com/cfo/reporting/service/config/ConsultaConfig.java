package com.cfo.reporting.service.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConsultaConfig {
    private String nombre;
    private String tabla;
    private String query;
    private List<ParametroConfig> parametros;
    private String tipoResultado;
    private String claveCache;
    private String valorCache;
    private ConfigCache configCache;

    public ConsultaConfig() {}

    // Getters y Setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTabla() {
        return tabla;
    }

    public void setTabla(String tabla) {
        this.tabla = tabla;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public List<ParametroConfig> getParametros() {
        return parametros;
    }

    public void setParametros(List<ParametroConfig> parametros) {
        this.parametros = parametros;
    }

    public String getTipoResultado() {
        return tipoResultado;
    }

    public void setTipoResultado(String tipoResultado) {
        this.tipoResultado = tipoResultado;
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

    public ConfigCache getConfigCache() {
        return configCache;
    }

    public void setConfigCache(ConfigCache configCache) {
        this.configCache = configCache;
    }

    public void validarParametros(Map<String, Object> parametrosUsuario) {
        if (this.parametros != null) {
            for (ParametroConfig paramConfig : this.parametros) {
                if (paramConfig.isObligatorio() &&
                        (parametrosUsuario == null || !parametrosUsuario.containsKey(paramConfig.getNombre()))) {
                    throw new IllegalArgumentException("Parámetro obligatorio no proporcionado: " + paramConfig.getNombre());
                }
            }
        }
    }

    public Map<String, Object> prepararParametros(Map<String, Object> parametrosUsuario) {
        Map<String, Object> parametrosPreparados = new HashMap<>();

        if (this.parametros != null) {
            for (ParametroConfig paramConfig : this.parametros) {
                Object valor = parametrosUsuario != null ? parametrosUsuario.get(paramConfig.getNombre()) : null;
                parametrosPreparados.put(paramConfig.getNombre(), paramConfig.convertirValor(valor));
            }
        }

        return parametrosPreparados;
    }
}