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

    public List<Resultado> procesar(List<DynamicValue> valoresDinamicos,
                                    Map<String, Map<String, Object>> tablas,
                                    String formulaPlantilla) {

        return valoresDinamicos.stream()
                .map(dv -> {
                    // 1. Crear contexto para este elemento
                    Map<String, String> contexto = crearContexto(dv);

                    // 2. Resolver fórmula
                    String formulaResuelta = resolverFormula(formulaPlantilla, contexto);

                    // 3. Evaluar expresión
                    List<CodeValueRec> resultado = evaluarFormula(formulaResuelta, tablas);

                    return new Resultado(resultado.get(0).code(), formulaResuelta, resultado.get(0).value(),resultado.get(1).value(),resultado.get(2).value());
                })
                .collect(Collectors.toList());
    }

    private Map<String, String> crearContexto(DynamicValue dv) {
        // Puedes expandir esto para manejar múltiples valores por objeto
        return Map.of(dv.getKey(), dv.getValue());
    }

    private String resolverFormula(String formula, Map<String, String> contexto) {
        // Patrón para encontrar #{clave}
        Pattern patron = Pattern.compile("#\\{([^}]+)\\}");

        return patron.matcher(formula)
                .replaceAll(match -> {
                    String clave = match.group(1);
                    return contexto.getOrDefault(clave, "null");
                });
    }

    private List<CodeValueRec> evaluarFormula(String formula, Map<String, Map<String, Object>> tablas) {
        // Usar tu implementación existente de evaluador estático
        StaticExpressionEvaluator evaluador = new StaticExpressionEvaluator(formula);
        return evaluador.evaluate(tablas);
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