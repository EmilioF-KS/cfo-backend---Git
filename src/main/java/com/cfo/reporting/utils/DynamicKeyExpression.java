package com.cfo.reporting.utils;

//import com.oracle.truffle.js.builtins.math.MathOperation;
//
//import java.util.*;
//import java.util.regex.*;
//
//public class DynamicKeyExpression {
//    private final String originalFormula;
//    private final List<LookupComponent> lookups;
//    private final List<MathOperation> operations;
//
//    public DynamicKeyExpression(String formula) {
//        this.originalFormula = formula;
//        this.lookups = parseFormula(formula);
//        this.operations = parseOperations(formula);
//    }
//
//    private List<LookupComponent> parseFormula(String formula) {
//        List<LookupComponent> components = new ArrayList<>();
//        // Regex para capturar VLOOKUP(dinámico, estático)
//        Pattern pattern = Pattern.compile("VLOOKUP\\(([^,]+),\\s*([^)]+)\\)");
//        Matcher matcher = pattern.matcher(formula);
//
//        while (matcher.find()) {
//            String keyExpression = matcher.group(1).trim();
//            String tableName = matcher.group(2).trim().replaceAll("['\"]", "");
//            components.add(new LookupComponent(keyExpression, tableName));
//        }
//
//        return components;
//    }
//
//    private List<MathOperation> parseOperations(String formula) {
//        List<MathOperation> ops = new ArrayList<>();
//        String[] parts = formula.split("(?<=VLOOKUP\\([^)]+\\))|(?=[-+*/])");
//
//        for (String part : parts) {
//            part = part.trim();
//            if (part.matches("[-+*/]")) {
//                ops.add(MathOperation.fromSymbol(part));
//            }
//        }
//
//        return ops;
//    }
//}*/
import com.cfo.reporting.dto.CodeValueRec;

import java.util.*;
import java.util.function.DoubleBinaryOperator;
import java.util.regex.*;

public class DynamicKeyExpression {
    private final String originalFormula;
    private final List<LookupComponent> lookups;
    private final List<MathOperation> operations;

    public DynamicKeyExpression(String formula) {
        this.originalFormula = formula;
        this.lookups = parseFormula(formula);
        this.operations = parseOperations(formula);
    }

    private List<LookupComponent> parseFormula(String formula) {
        List<LookupComponent> components = new ArrayList<>();
        // Regex para capturar VLOOKUP(dinámico, estático)
        Pattern pattern = Pattern.compile("VLOOKUP\\(([^,]+),\\s*([^)]+)\\)");
        Matcher matcher = pattern.matcher(formula);

        while (matcher.find()) {
            String keyExpression = matcher.group(1).trim();
            String tableName = matcher.group(2).trim().replaceAll("['\"]", "");
            components.add(new LookupComponent(keyExpression, tableName));
        }

        return components;
    }

    private List<MathOperation> parseOperations(String formula) {
        List<MathOperation> ops = new ArrayList<>();
        String[] parts = formula.split("(?<=VLOOKUP\\([^)]\\+\\))|(?=[-+*])");

        for (String part : parts) {
            part = part.trim();
            if (part.matches("[-+*/]")) {
                ops.add(MathOperation.fromSymbol(part));
            }
        }

        return ops;
    }

    public CodeValueRec evaluate(Map<String, Map<String, Object>> dataTables,
                           Map<String, String> keyContext) {
        // Resolver claves dinámicas
        List<ResolvedLookup> resolvedLookups = resolveLookups(keyContext);

        // Construir fórmula con claves resueltas
        String resolvedFormula = buildResolvedFormula(resolvedLookups);

        // Evaluar la fórmula resultante
        return evaluateResolvedFormula(resolvedFormula, dataTables);
    }

    private List<ResolvedLookup> resolveLookups(Map<String, String> context) {
        List<ResolvedLookup> resolved = new ArrayList<>();

        for (LookupComponent component : lookups) {
            String resolvedKey = resolveKey(component.keyExpression(), context);
            resolved.add(new ResolvedLookup(resolvedKey, component.tableName()));
        }

        return resolved;
    }

    private String resolveKey(String keyExpression, Map<String, String> context) {
        // Si la expresión es una referencia (ej: #{producto})
        if (keyExpression.startsWith("#{") && keyExpression.endsWith("}")) {
            String key = keyExpression.substring(2, keyExpression.length() - 1);
            return context.getOrDefault(key, key);
        }
        // Si es un valor directo (elimina comillas si existen)
        return keyExpression.replaceAll("['\"]", "");
    }

    private String buildResolvedFormula(List<ResolvedLookup> resolvedLookups) {
        String formula = originalFormula;
        int lookupIndex = 0;

        // Reemplazar cada VLOOKUP con sus valores resueltos
        Matcher matcher = Pattern.compile("VLOOKUP\\([^)]+\\)").matcher(formula);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            ResolvedLookup resolved = resolvedLookups.get(lookupIndex++);
            matcher.appendReplacement(sb,
                    String.format("VLOOKUP('%s', %s)", resolved.key(), resolved.tableName()));
        }
        matcher.appendTail(sb);

        return sb.toString();
    }

    private CodeValueRec evaluateResolvedFormula(String formula, Map<String, Map<String, Object>> dataTables) {
        // Usar un evaluador simple para fórmulas estáticas
        StaticExpressionEvaluator evaluator = new StaticExpressionEvaluator(formula,dataTables);
        return evaluator.evaluate(dataTables);
    }

    // Clases de soporte
    private record LookupComponent(String keyExpression, String tableName) {}
    private record ResolvedLookup(String key, String tableName) {}

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
}
