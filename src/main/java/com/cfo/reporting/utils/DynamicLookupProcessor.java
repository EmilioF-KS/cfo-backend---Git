package com.cfo.reporting.utils;

import com.cfo.reporting.dto.CodeValueRec;
import lombok.Data;

import java.util.*;
import java.util.regex.*;
import java.util.stream.*;

public class DynamicLookupProcessor {

    // Clase para representar los objetos con valores dinámicos
    public static class DynamicValue {
        private final String key;
        private final String value;

        public DynamicValue(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() { return key; }
        public String getValue() { return value; }

        @Override
        public String toString() {
            return this.key+" -> "+this.value;
        }
    }

    public CodeValueRec procesar(DynamicValue valorDinamico,
                                    Map<String, Map<String, Object>> tablas,
                                    String formulaPlantilla) {

            // 1. Crear contexto para este elemento
            Map<String, String> contexto = crearContexto(valorDinamico.getKey(),valorDinamico.getValue());
            // 2. Resolver fórmula
           String formulaResuelta = resolverFormula(formulaPlantilla, contexto);
           CodeValueRec resultados = evaluarFormula(formulaResuelta, tablas);

            return resultados;
    }

    private Map<String, String> crearContexto(String key, String value) {
        // Puedes expandir esto para manejar múltiples valores por objeto
        Map<String,String> mapContextos = new HashMap<>();
        if (value!= null && !value.isEmpty()) {
            String[] values = value.split(",");
            if (values.length > 1) {
                int index = 1;
                for (String valueToReturn : values) {
                    mapContextos.put(key +"_"+ index, valueToReturn);
                    index++;
                }
                return mapContextos;
            }
        } else {
            value="";
        }
        return Map.of(key,value);
    }

    private String resolverFormula(String formula, Map<String, String> contexto) {
        // Patrón para encontrar #{clave}
        Pattern patron = Pattern.compile("#\\{([^}]+)\\}");

        return patron.matcher(formula)
                .replaceAll(match -> {
                    String clave = match.group(1);
                    System.out.println("clave "+clave+" "+contexto.get(clave));
                    return contexto.getOrDefault(clave, "null");
                });
    }

    private CodeValueRec evaluarFormula(String formula, Map<String, Map<String, Object>> tablas) {
        // Usar tu implementación existente de evaluador estático
        StaticExpressionEvaluator evaluador = new StaticExpressionEvaluator(formula,tablas);
        CodeValueRec codeValueRec = evaluador.parseFormula(formula);

        return codeValueRec;
    }


    // Clase para almacenar resultados
    @Data
    public static class Resultado {
        private final String clave;
        private final String formula;
        private final double valLookupFirst;
        private final double valLookupSecond;
        private final double valor;

        public Resultado(String clave, String formula, double valLookupFirst, double valLookupSecond, double valor) {
            this.clave = clave;
            this.formula = formula;
            this.valLookupFirst = valLookupFirst;
            this.valLookupSecond = valLookupSecond;
            this.valor = valor;
        }

        // Getters
        public String getClave() { return clave; }
        public String getFormula() { return formula; }
        public double getValor() { return valor; }
        public String toString() {
            return "[ clave ="+clave+" "+
            "formula ="+formula+
            "tot_current_balance actual= "+valLookupFirst+
            "tot_current_balance anterior= "+valLookupSecond+
            " Resultado operacin="+ valor;
        }
    }

}