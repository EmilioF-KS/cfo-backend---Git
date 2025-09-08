package com.cfo.reporting.service.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Arrays;
import java.util.List;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;

public class ParametroConfig {
    private String nombre;
    private String tipo;
    private Object valorPorDefecto;
    private boolean obligatorio;

    // Constructor por defecto
    public ParametroConfig() {}

    // Getters y Setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Object getValorPorDefecto() {
        return valorPorDefecto;
    }

    public void setValorPorDefecto(Object valorPorDefecto) {
        this.valorPorDefecto = valorPorDefecto;
    }

    public boolean isObligatorio() {
        return obligatorio;
    }

    @JsonProperty("obligatorio")
    public void setObligatorio(boolean obligatorio) {
        this.obligatorio = obligatorio;
    }

    // Método para convertir el valor al tipo correcto
    public Object convertirValor(Object valor) {
        if (valor == null) {
            return obtenerValorPorDefectoConvertido();
        }

        try {
            switch (tipo.toUpperCase()) {
                case "STRING":
                    return valor.toString();
                case "INTEGER":
                case "INT":
                    if (valor instanceof Number) {
                        return ((Number) valor).intValue();
                    }
                    return Integer.parseInt(valor.toString());
                case "LONG":
                    if (valor instanceof Number) {
                        return ((Number) valor).longValue();
                    }
                    return Long.parseLong(valor.toString());
                case "DOUBLE":
                case "FLOAT":
                    if (valor instanceof Number) {
                        return ((Number) valor).doubleValue();
                    }
                    return Double.parseDouble(valor.toString());
                case "BOOLEAN":
                case "BOOL":
                    if (valor instanceof Boolean) {
                        return valor;
                    }
                    return Boolean.parseBoolean(valor.toString());
                case "DATE":
                    if (valor instanceof Date) {
                        return valor;
                    }
                    // Intentar parsear la fecha
                    try {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        return dateFormat.parse(valor.toString());
                    } catch (ParseException e) {
                        throw new IllegalArgumentException("Formato de fecha inválido para el parámetro " + nombre + ". Use formato yyyy-MM-dd.");
                    }
                case "LISTA_STRING":
                case "STRING_LIST":
                    if (valor instanceof List) {
                        return valor;
                    } else if (valor instanceof String) {
                        // Podría ser una cadena separada por comas
                        return Arrays.asList(((String) valor).split("\\s*,\\s*"));
                    } else if (valor instanceof String[]) {
                        return Arrays.asList((String[]) valor);
                    }
                    return valor;
                case "LISTA_INTEGER":
                case "INTEGER_LIST":
                    if (valor instanceof List) {
                        return valor;
                    } else if (valor instanceof String) {
                        // Convertir cadena separada por comas a lista de enteros
                        String[] partes = ((String) valor).split("\\s*,\\s*");
                        Integer[] enteros = new Integer[partes.length];
                        for (int i = 0; i < partes.length; i++) {
                            enteros[i] = Integer.parseInt(partes[i]);
                        }
                        return Arrays.asList(enteros);
                    } else if (valor instanceof int[]) {
                        int[] arrayInt = (int[]) valor;
                        Integer[] enteros = new Integer[arrayInt.length];
                        for (int i = 0; i < arrayInt.length; i++) {
                            enteros[i] = arrayInt[i];
                        }
                        return Arrays.asList(enteros);
                    }
                    return valor;
                default:
                    return valor;
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Error al convertir parámetro " + nombre + " al tipo " + tipo + ": " + e.getMessage(), e);
        }
    }

    private Object obtenerValorPorDefectoConvertido() {
        if (valorPorDefecto == null) {
            return null;
        }

        // Convertir el valor por defecto al tipo correcto
        return convertirValor(valorPorDefecto);
    }

    @Override
    public String toString() {
        return "ParametroConfig{" +
                "nombre='" + nombre + '\'' +
                ", tipo='" + tipo + '\'' +
                ", valorPorDefecto=" + valorPorDefecto +
                ", obligatorio=" + obligatorio +
                '}';
    }
}
