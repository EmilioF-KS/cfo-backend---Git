package com.cfo.reporting.dto;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ConceptRecord {
    private String descripcion;
    private Map<String, Object> valoresColumnas = new LinkedHashMap<>(); // Mantiene orden
    private List<DetalleEjecucion> detalles = new ArrayList<>();

    // Clase para detalles de ejecución
    public static class DetalleEjecucion {
        private String columna;
        private String formula;
        private String tipo;
        private Object valorObtenido;
        private String tablaReferenciada;
    }

    // Métodos utilitarios
    public void agregarValor(String columna, Object valor, String formula, String tipo, String tabla) {
        valoresColumnas.put(columna, valor);
        DetalleEjecucion detalle = new DetalleEjecucion();
        detalle.columna = columna;
        detalle.formula = formula;
        detalle.tipo = tipo;
        detalle.valorObtenido = valor;
        detalle.tablaReferenciada = tabla;
        detalles.add(detalle);
    }
}
