package com.cfo.reporting.utils;

import java.util.*;
import java.util.regex.*;
import java.util.stream.*;

public class EnhancedDynamicLookupProcessor {

    public static class ProcessingResult {
        private final List<String> lookupKeys;
        private final List<Double> lookupValues;
        private final double finalResult;

        public ProcessingResult(List<String> lookupKeys,
                                List<Double> lookupValues,
                                double finalResult) {
            this.lookupKeys = Collections.unmodifiableList(lookupKeys);
            this.lookupValues = Collections.unmodifiableList(lookupValues);
            this.finalResult = finalResult;
        }

        // Getters
        public List<String> getLookupKeys() { return lookupKeys; }
        public List<Double> getLookupValues() { return lookupValues; }
        public double getFinalResult() { return finalResult; }

        @Override
        public String toString() {
            return String.format("Claves buscadas: %s | Valores encontrados: %s | Resultado final: %.2f",
                    lookupKeys, lookupValues, finalResult);
        }
    }

    public static class LookupData {
        private final String key;
        private final String tableName;

        public LookupData(String key, String tableName) {
            this.key = key;
            this.tableName = tableName;
        }

        public String getKey() { return key; }
        public String getTableName() { return tableName; }
    }

    public ProcessingResult process(String formulaTemplate,
                                    Map<String, String> dynamicValues,
                                    Map<String, Map<String, Double>> dataTables) {

        // 1. Resolver valores dinámicos en la fórmula
        String resolvedFormula = resolveFormula(formulaTemplate, dynamicValues);

        // 2. Extraer información de los VLOOKUPs
        List<LookupData> lookups = extractLookups(resolvedFormula);

        // 3. Obtener valores de las tablas
        List<Double> foundValues = resolveLookupValues(lookups, dataTables);

        // 4. Calcular resultado final
        double result = evaluateExpression(replaceLookupsWithValues(resolvedFormula, lookups, foundValues));

        // 5. Extraer claves buscadas
        List<String> searchedKeys = lookups.stream()
                .map(LookupData::getKey)
                .collect(Collectors.toList());

        return new ProcessingResult(searchedKeys, foundValues, result);
    }

    private String resolveFormula(String template, Map<String, String> dynamicValues) {
        Pattern pattern = Pattern.compile("#\\{([^}]+)\\}");
        Matcher matcher = pattern.matcher(template);

        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String key = matcher.group(1);
            String value = dynamicValues.getOrDefault(key, "null");
            matcher.appendReplacement(sb, Matcher.quoteReplacement(value));
        }
        matcher.appendTail(sb);

        return sb.toString();
    }

    private List<LookupData> extractLookups(String formula) {
        Pattern pattern = Pattern.compile("VLOOKUP\\(['\"]?([^'\",]+)['\"]?,\\s*['\"]?([^'\")]+)['\"]?\\)");
        Matcher matcher = pattern.matcher(formula);

        List<LookupData> lookups = new ArrayList<>();
        while (matcher.find()) {
            lookups.add(new LookupData(matcher.group(1), matcher.group(2)));
        }
        return lookups;
    }

    private List<Double> resolveLookupValues(List<LookupData> lookups,
                                             Map<String, Map<String, Double>> dataTables) {
        return lookups.stream()
                .map(lookup -> {
                    Map<String, Double> table = dataTables.get(lookup.getTableName());
                    if (table == null) {
                        throw new IllegalArgumentException("Tabla no encontrada: " + lookup.getTableName());
                    }

                    Double value = table.get(lookup.getKey());
                    if (value == null) {
                       System.out.println(
                                String.format("Clave '%s' no encontrada en tabla '%s'",
                                        lookup.getKey(), lookup.getTableName()));
                    }
                    return value;
                })
                .collect(Collectors.toList());
    }

    private String replaceLookupsWithValues(String formula,
                                            List<LookupData> lookups,
                                            List<Double> values) {
        String result = formula;
        for (int i = 0; i < lookups.size(); i++) {
            String lookupPattern = "VLOOKUP\\(['\"]?" +
                    Pattern.quote(lookups.get(i).getKey()) +
                    "['\"]?,\\s*['\"]?" +
                    Pattern.quote(lookups.get(i).getTableName()) +
                    "['\"]?\\)";
            result = result.replaceAll(lookupPattern, values.get(i).toString());
        }
        return result;
    }

    private double evaluateExpression(String expression) {
        // Implementación básica - considerar usar una librería como exp4j para casos complejos
        String[] tokens = expression.split("(?<=[-+*/])|(?=[-+*/])");
        double result = Double.parseDouble(tokens[0].trim());

        for (int i = 1; i < tokens.length; i += 2) {
            String operator = tokens[i].trim();
            double operand = Double.parseDouble(tokens[i + 1].trim());

            switch (operator) {
                case "+": result += operand; break;
                case "-": result -= operand; break;
                case "*": result *= operand; break;
                case "/": result /= operand; break;
                default: throw new IllegalArgumentException("Operador no soportado: " + operator);
            }
        }

        return result;
    }
}
