package com.cfo.reporting.drools.vlookup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.DoubleBinaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParsedExpression {
    private final String formula;
    private final List<ExpressionComponent> components;
    private final List<Operator> operators;

    public ParsedExpression(String formula) {
        this.formula = formula.trim();
        this.components = new ArrayList<>();
        this.operators = new ArrayList<>();
        parseFormula();
    }

    private void parseFormula() {
        // Regex mejorado para detectar VLOOKUPs, números (incluyendo decimales) y operadores
        Pattern pattern = Pattern.compile("(VLOOKUP\\([^)]+\\)|\\d+\\.?\\d*|[-+*/])");
        Matcher matcher = pattern.matcher(formula);

        List<String> tokens = new ArrayList<>();
        while (matcher.find()) {
            tokens.add(matcher.group());
        }

        // Validar que los tokens alternen entre componentes y operadores
        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i);
            if (i % 2 == 0) {
                // Debe ser un componente (VLOOKUP o constante)
                if (token.startsWith("VLOOKUP")) {
                    components.add(parseVLookup(token));
                } else if (token.matches("\\d+\\.?\\d*")) {
                    components.add(new ConstantComponent(Double.parseDouble(token)));
                } else {
                    throw new IllegalArgumentException("Se esperaba VLOOKUP o constante en posición " + (i + 1));
                }
            } else {
                // Debe ser un operador
                if (isOperator(token)) {
                    operators.add(Operator.fromSymbol(token));
                } else {
                    throw new IllegalArgumentException("Se esperaba operador en posición " + (i + 1));
                }
            }
        }

        validateStructure();
    }

    private boolean isOperator(String token) {
        return token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/");
    }

    private VLookupComponent parseVLookup(String vlookupStr) {
        String params = vlookupStr.substring(8, vlookupStr.length() - 1);
        String[] parts = params.split("\\s*,\\s*");

        if (parts.length != 2) {
            throw new IllegalArgumentException("VLOOKUP debe tener 2 parámetros: clave y tabla");
        }

        return new VLookupComponent(parts[0], parts[1]);
    }

    private void validateStructure() {
        if (components.isEmpty()) {
            throw new IllegalArgumentException("La fórmula no contiene componentes válidos");
        }
        if (components.size() != operators.size() + 1) {
            throw new IllegalArgumentException("Número incorrecto de operadores en la fórmula");
        }
    }

    public double evaluate(Map<String, Map<String, Double>> lookupTables,
                           Map<String, String> context) {
        if (components.isEmpty()) {
            return 0;
        }

        // Evaluar el primer componente
        double result = evaluateComponent(components.get(0), lookupTables, context);

        // Aplicar operaciones secuencialmente
        for (int i = 0; i < operators.size(); i++) {
            double nextValue = evaluateComponent(components.get(i + 1), lookupTables, context);
            result = operators.get(i).apply(result, nextValue);
        }

        return result;
    }

    private double evaluateComponent(ExpressionComponent component,
                                     Map<String, Map<String, Double>> lookupTables,
                                     Map<String, String> context) {
        if (component instanceof VLookupComponent) {
            return evaluateVLookup((VLookupComponent) component, lookupTables, context);
        } else if (component instanceof ConstantComponent) {
            return ((ConstantComponent) component).getValue();
        }
        throw new IllegalArgumentException("Tipo de componente no soportado: " + component.getClass());
    }

    private double evaluateVLookup(VLookupComponent component,
                                   Map<String, Map<String, Double>> lookupTables,
                                   Map<String, String> context) {
        String key = context.get(component.getKey());
        String tableName = component.getTableName();

        if (key == null) {
            throw new IllegalArgumentException("Clave '" + component.getKey() + "' no encontrada en contexto");
        }

        Map<String, Double> table = lookupTables.get(tableName);
        if (table == null) {
            throw new IllegalArgumentException("Tabla '" + tableName + "' no encontrada");
        }

        Double value = table.get(key);
        if (value == null) {
            throw new IllegalArgumentException("Clave '" + key + "' no encontrada en tabla '" + tableName + "'");
        }

        return value;
    }

    // Interfaces y clases internas
    private interface ExpressionComponent {}

    private static class VLookupComponent implements ExpressionComponent {
        private final String key;
        private final String tableName;

        public VLookupComponent(String key, String tableName) {
            this.key = key;
            this.tableName = tableName;
        }

        public String getKey() {
            return key;
        }

        public String getTableName() {
            return tableName;
        }
    }

    private static class ConstantComponent implements ExpressionComponent {
        private final double value;

        public ConstantComponent(double value) {
            this.value = value;
        }

        public double getValue() {
            return value;
        }
    }

    private enum Operator {
        ADD("+", (a, b) -> a + b),
        SUBTRACT("-", (a, b) -> a - b),
        MULTIPLY("*", (a, b) -> a * b),
        DIVIDE("/", (a, b) -> a / b);

        private final String symbol;
        private final DoubleBinaryOperator operation;

        Operator(String symbol, DoubleBinaryOperator operation) {
            this.symbol = symbol;
            this.operation = operation;
        }

        public double apply(double a, double b) {
            return operation.applyAsDouble(a, b);
        }

        public static Operator fromSymbol(String symbol) {
            for (Operator op : values()) {
                if (op.symbol.equals(symbol)) {
                    return op;
                }
            }
            throw new IllegalArgumentException("Operador no soportado: " + symbol);
        }
    }
}