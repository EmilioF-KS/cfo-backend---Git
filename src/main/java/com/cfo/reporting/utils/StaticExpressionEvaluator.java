package com.cfo.reporting.utils;

import com.cfo.reporting.dto.CodeValueRec;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.DoubleBinaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StaticExpressionEvaluator {
    private  String formula;
    private  List<LookupOperation> lookups;
    private  List<MathOperation> operations;
    private Map<String, Double> valoresReferencia;
    private Map<String,Map<String,Object>> tablesReferences = new HashMap<>();
    private  Map<String, Integer> precedenciaOperadores;

    public StaticExpressionEvaluator(String formula,Map<String,Map<String,Object>> tables) {
        this.tablesReferences = tables;
        this.valoresReferencia = new HashMap<>();
        this.formula = formula;
        this.lookups = parseLookups(formula);
        this.operations = parseOperations(formula);
        // Definir precedencia de operadores
        this.precedenciaOperadores = new HashMap<>();
        precedenciaOperadores.put("+", 1);
        precedenciaOperadores.put("-", 1);
        precedenciaOperadores.put("*", 2);
        precedenciaOperadores.put("/", 2);
        precedenciaOperadores.put("^", 3);
        // Potencia
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

    private List<String> infixToPostfix(List<String> tokens) {
        List<String> output = new ArrayList<>();
        Stack<String> stack = new Stack<>();

        for (String token : tokens) {
            if (isNumber(token) || isVariable(token)) {
                output.add(token);
            } else if (token.equals("ROUND")) {
                stack.push(token);
            } else if (token.equals(",")) {
                // Para manejar argumentos de funciones
                while (!stack.isEmpty() && !stack.peek().equals("(")) {
                    output.add(stack.pop());
                }
            } else if (isOperator(token)) {
                while (!stack.isEmpty() && getPrecedence(stack.peek()) >= getPrecedence(token)) {
                    output.add(stack.pop());
                }
                stack.push(token);
            } else if (token.equals("(")) {
                stack.push(token);
            } else if (token.equals(")")) {
                while (!stack.isEmpty() && !stack.peek().equals("(")) {
                    output.add(stack.pop());
                }
                stack.pop(); // Remover el "("
                if (!stack.isEmpty() && isFunction(stack.peek())) {
                    output.add(stack.pop()); // Añadir la función al output
                }
            }
        }

        while (!stack.isEmpty()) {
            output.add(stack.pop());
        }

        return output;
    }
    private boolean isFunction(String token) {
        return token.equalsIgnoreCase("ROUND") ||
                token.equalsIgnoreCase("VLOOKUP") ||
                token.equalsIgnoreCase("SUM") ||
                token.equalsIgnoreCase("AVG"); // añade otras funciones si necesitas
    }



    private boolean isNumber(String token) {
        return token.matches("-?\\d+(\\.\\d+)?([eE][-+]?\\d+)?");
    }

    private boolean isVariable(String token) {
        return token.matches("\b(?!(ROUND)\b)[a-zA-Z_]*\\d+[a-zA-Z0-9_]+\b|\b*[A-Za-z]+d+[A-Za-z]*\b");
    }

    private boolean isOperator(String token) {
        return precedenciaOperadores.containsKey(token);
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

        // Evaluar todos los VLOOKUPs primero
        for (LookupOperation lookup : lookups) {
            values.add(resolveLookup(lookup, dataTables));
        }
        // Aplicar operaciones matemáticas

        double result =0d;
        CodeValueRec resultado = parseFormula(this.formula);
 //       double result = evaluarFormulaBusqueda(this.formula, dataTables);
//        for (int i = 0; i < operations.size(); i++) {
//            result = operations.get(i).apply(values.get(i).value(), values.get(i + 1).value());
//        }
       // --
        //
        if (lookups.size() > 0 && values.size() > 0 && result == 0) {
            codeValueResult = new CodeValueRec(lookups.get(0).key(), result);
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


    public CodeValueRec parseFormula(String formula) {
        List<String> tokens = tokenizePreservingFunctions(formula);

        List<String> processedTokens = processVlookupTokens(tokens);
        String formulaSinVlookups = rebuildFormula(processedTokens);

        if (!formulaSinVlookups.isEmpty() && formulaSinVlookups.length() == 3 ) {
                return new CodeValueRec("NoCode",Double.valueOf(formulaSinVlookups));
        }
        List<String> postfijo = infixToPostfix(processedTokens);
        double resultado = evaluatePostfix(postfijo);

        return new CodeValueRec("Code", Double.valueOf(String.valueOf(resultado)));
    }

    private int getPrecedence(String operator) {
        switch (operator) {
            case "ROUND": return 5;
            case "^": return 4;
            case "*":
            case "/": return 3;
            case "+":
            case "-": return 2;
            default: return 0;
        }
    }
    private List<String> processVlookupTokens(List<String> tokens) {
        List<String> result = new ArrayList<>();
        int i = 0;

        while (i < tokens.size()) {
            if (tokens.get(i).equalsIgnoreCase("VLOOKUP")) {
                // Reconstruir la expresión VLOOKUP completa
                StringBuilder vlookupExpr = new StringBuilder();
                vlookupExpr.append(tokens.get(i)); // VLOOKUP

                int j = i + 1;
                int parenCount = 0;

                // Añadir tokens hasta completar el VLOOKUP
                while (j < tokens.size() && parenCount >= 0) {
                    vlookupExpr.append(tokens.get(j));
                    if (tokens.get(j).equals("(")) parenCount++;
                    else if (tokens.get(j).equals(")")) parenCount--;
                    j++;
                    if (parenCount == 0) break;
                }

                // Evaluar el VLOOKUP
                Double value = parseAndEvaluateVlookup(vlookupExpr.toString());
                result.add(value != null ? value.toString() : "0");
                i = j;
            } else {
                result.add(tokens.get(i));
                i++;
            }
        }

        return result;
    }

    private Double parseAndEvaluateVlookup(String vlookupExpr) {
        // Extraer parámetros del VLOOKUP
        Pattern pattern = Pattern.compile("VLOOKUP\\s*\\(\\s*([^,]+)\\s*,\\s*([^)]+)\\s*\\)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(vlookupExpr);

        try {
            if (matcher.find()) {
                String key = matcher.group(1).trim();
                String table = matcher.group(2).trim();
                BigDecimal valueFound = new BigDecimal(String.valueOf(this.tablesReferences.get(table).get(key)));
                double valueDouble = valueFound.doubleValue();
                return valueFound.doubleValue();
            }
        }
        catch(Exception ex) {
            return 0d;
        }
        return 0d;
    }

    private List<String> tokenizePreservingFunctions(String formula) {
        List<String> tokens = new ArrayList<>();
        String regex = "(VLOOKUP|ROUND|\\d+(?:\\.\\d+)?(?:-\\d+)*|[a-zA-Z_][a-zA-Z0-9_]*|[-+*/^(),])";

        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(formula);

        while (matcher.find()) {
            tokens.add(matcher.group());
        }
        return tokens;
    }



    private String rebuildFormula(List<String> tokens) {
        StringBuilder sb = new StringBuilder();
        for (String token : tokens) {
            sb.append(token);
        }
        return sb.toString();
    }

    private double evaluatePostfix(List<String> postfix) {
        Stack<Double> stack = new Stack<>();

        for (String token : postfix) {
            if (isNumber(token)) {
                stack.push(Double.parseDouble(token));
            } else if (isFunction(token)) {
                if (token.equalsIgnoreCase("ROUND")) {
                    if (stack.size() < 1) {
                        throw new RuntimeException("ROUND necesita un argumento");
                    }
                    double value = stack.pop();
                    stack.push(Double.valueOf(Math.round(value)));
                }
                // Añadir otras funciones aquí si las necesitas
            } else if (isOperator(token)) {
                if (stack.size() < 2) {
                    throw new RuntimeException("Operador " + token + " necesita dos operandos");
                }
                double b = stack.pop();
                double a = stack.pop();
                switch (token) {
                    case "+": stack.push(a + b); break;
                    case "-": stack.push(a - b); break;
                    case "*": stack.push(a * b); break;
                    case "/":
                        if (b == 0) throw new RuntimeException("División por cero");
                        stack.push(a / b);
                        break;
                    case "^": stack.push(Math.pow(a, b)); break;
                    default:
                        throw new RuntimeException("Operador desconocido: " + token);
                }
            } else {
                throw new RuntimeException("Token desconocido: " + token);
            }
        }

        if (stack.size() != 1) {
            throw new RuntimeException("Expresión mal formada");
        }

        return stack.pop();
    }
}
