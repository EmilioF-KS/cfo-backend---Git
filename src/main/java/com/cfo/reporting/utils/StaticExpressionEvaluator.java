package com.cfo.reporting.utils;

import com.cfo.reporting.dto.CodeValueRec;

import java.util.*;
import java.util.function.DoubleBinaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StaticExpressionEvaluator {
    private final String formula;
    private final List<LookupOperation> lookups;
    private final List<MathOperation> operations;

    public StaticExpressionEvaluator(String formula) {
        this.formula = formula;
        this.lookups = parseLookups(formula);
        this.operations = parseOperations(formula);
    }

    private List<LookupOperation> parseLookups(String formula) {
        List<LookupOperation> foundLookups = new ArrayList<>();
        Pattern pattern = Pattern.compile("VLOOKUP\\(([^,]+),\\s*([^)]+)\\)");
        Matcher matcher = pattern.matcher(formula);

        while (matcher.find()) {
            String key = matcher.group(1).trim().replaceAll("'|\"", ""); // Elimina comillas
            String table = matcher.group(2).trim();
            foundLookups.add(new LookupOperation(key, table));
        }

        return foundLookups;
    }

    private List<MathOperation> parseOperations(String formula) {
        List<MathOperation> ops = new ArrayList<>();
        // Busca operadores entre los VLOOKUPs o valores
        List<String> parts  = splitFormula(formula);
        for (String part : parts) {
            part = part.trim();
            if (part.matches("[-+*/]")) {
                ops.add(MathOperation.fromSymbol(part));
            }
        }

        return ops;
    }

    public CodeValueRec evaluate(Map<String, Map<String, Object>> dataTables) {
        List<CodeValueRec> values = new ArrayList<>();
        List<CodeValueRec> resultValues = new ArrayList<>();
        CodeValueRec codeValueResult;
        String codeValue = "";
        // Evaluar todos los VLOOKUPs primero
        for (LookupOperation lookup : lookups) {
            values.add(resolveLookup(lookup, dataTables));
        }
        // Aplicar operaciones matemáticas
        double result = 0;
        for (int i = 0; i < operations.size(); i++) {
            result = operations.get(i).apply(values.get(i).value(), values.get(i + 1).value());
        }
        //
        if (lookups.size() > 0 && values.size() > 0 && result == 0) {
            codeValueResult = new CodeValueRec(lookups.get(0).key(), values.get(0).value());
        } else {
            codeValueResult = new CodeValueRec("No", 0d);
        }
        return codeValueResult;
    }

    private CodeValueRec resolveLookup(LookupOperation lookup, Map<String, Map<String, Object>> dataTables) {
        Map<String, Object> table = dataTables.get(lookup.tableName());

        String keyTolook = lookup.key().toString();
        if (table == null) {
            return new CodeValueRec(keyTolook.toString(),0d);
            //throw new IllegalArgumentException("Tabla no encontrada: " + lookup.tableName());
        }
        Double value = (Double)table.get(keyTolook);
        if (value == null) {
            System.out.println("Key '" + lookup.key() + "' not found in table '" + lookup.tableName() + "'");
            value=0d;
        }


        return new CodeValueRec(keyTolook.toString(),value);
    }

    // Clases de soporte
    private record LookupOperation(String key, String tableName) {}

    private enum MathOperation {
        ADD("+", (a, b) -> a + b),
        SUBTRACT("-", (a, b) -> a - b),
        MULTIPLY("*", (a, b) -> a * b),
        DIVIDE("/", (a, b) -> a / b);

        private final String symbol;
        private final DoubleBinaryOperator operation;

        MathOperation(String symbol, DoubleBinaryOperator operation) {
            this.symbol = symbol;
            this.operation = operation;
        }

        public double apply(double a, double b) {
            return operation.applyAsDouble(a, b);
        }

        public static MathOperation fromSymbol(String symbol) {
            return Arrays.stream(values())
                    .filter(op -> op.symbol.equals(symbol))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Operador no soportado: " + symbol));
        }
    }

    public static List<String> splitFormula(String formula) {
        List<String> parts = new ArrayList<>();
        StringBuilder currentPart = new StringBuilder();
        int parenthesisLevel = 0;
        boolean insideVlookup = false;

        for (int i = 0; i < formula.length(); i++) {
            char c = formula.charAt(i);

            if (c == '(') {
                parenthesisLevel++;
                if (formula.substring(Math.max(0, i - 8), i).trim().equalsIgnoreCase("VLOOKUP")) {
                    insideVlookup = true;
                }
            } else if (c == ')') {
                parenthesisLevel--;
                if (parenthesisLevel == 0 && insideVlookup) {
                    insideVlookup = false;
                }
            }

            currentPart.append(c);

            // Detectamos operadores fuera de VLOOKUP
            if (!insideVlookup && (c == '+' || c == '-' || c == '*' || c == '/')) {
                // Guardamos la parte antes del operador
                parts.add(currentPart.substring(0, currentPart.length() - 1).trim());
                // Guardamos el operador
                parts.add(String.valueOf(c));
                // Reiniciamos para la siguiente parte
                currentPart = new StringBuilder();
            }
        }

        // Añadimos el último fragmento
        if (currentPart.length() > 0) {
            parts.add(currentPart.toString().trim());
        }

        return parts;
    }
}
