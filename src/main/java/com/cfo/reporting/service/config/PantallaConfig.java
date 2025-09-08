package com.cfo.reporting.service.config;

import java.util.List;

public class PantallaConfig {
    private String clavePantalla;
    private String descripcion;
    private List<ConsultaConfig> consultas;

    public PantallaConfig() {}

    // Getters y Setters
    public String getClavePantalla() {
        return clavePantalla;
    }

    public void setClavePantalla(String clavePantalla) {
        this.clavePantalla = clavePantalla;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public List<ConsultaConfig> getConsultas() {
        return consultas;
    }

    public void setConsultas(List<ConsultaConfig> consultas) {
        this.consultas = consultas;
    }

    public ConsultaConfig obtenerConsultaPorNombre(String nombreConsulta) {
        return consultas.stream()
                .filter(consulta -> consulta.getNombre().equals(nombreConsulta))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Consulta no encontrada: " + nombreConsulta));
    }
}
