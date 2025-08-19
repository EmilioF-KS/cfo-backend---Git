package com.cfo.reporting.drools.vlookup;


import com.cfo.reporting.drools.vlookup.model.data.EvaluationContext;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * Representa un operador en una expresión con sus operandos
 */
public class OperatorExpression implements ExpressionPart {
    private final String operator;
    private final ExpressionPart leftOperand;
    private final ExpressionPart rightOperand;
    private static final List<String> SUPPORTED_OPERATORS =
            Arrays.asList("+", "-", "*", "/", "^", "=", "<", ">", "<=", ">=", "<>");

    public OperatorExpression(String operator, ExpressionPart leftOperand, ExpressionPart rightOperand) {
        if (!SUPPORTED_OPERATORS.contains(operator)) {
            throw new IllegalArgumentException("Operador no soportado: " + operator);
        }
        this.operator = operator;
        this.leftOperand = leftOperand;
        this.rightOperand = rightOperand;
    }

    @Override
    public String getText() {
        return leftOperand.getText() + " " + operator + " " + rightOperand.getText();
    }

    @Override
    public Object evaluate(EvaluationContext context) {
        Object leftValue = leftOperand.evaluate(context);
        Object rightValue = rightOperand.evaluate(context);

        // Conversión a números para operaciones aritméticas
        if (isArithmeticOperator()) {
            BigDecimal leftNumber = convertToNumber(leftValue);
            BigDecimal rightNumber = convertToNumber(rightValue);
            return evaluateArithmetic(leftNumber, rightNumber);
        }

        // Operadores de comparación
        return evaluateComparison(leftValue, rightValue);
    }

    private boolean isArithmeticOperator() {
        return operator.matches("[+\\-*/^]");
    }

    private BigDecimal convertToNumber(Object value) {
        if (value instanceof Number) {
            return value instanceof BigDecimal ? (BigDecimal) value :
                    new BigDecimal(value.toString());
        }
        try {
            return new BigDecimal(value.toString());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "No se puede convertir a número: " + value);
        }
    }

    private BigDecimal evaluateArithmetic(BigDecimal left, BigDecimal right) {
        switch (operator) {
            case "+": return left.add(right);
            case "-": return left.subtract(right);
            case "*": return left.multiply(right);
            case "/": return left.divide(right, 10, BigDecimal.ROUND_HALF_UP);
            case "^": return left.pow(right.intValue());
            default: throw new UnsupportedOperationException(
                    "Operador aritmético no implementado: " + operator);
        }
    }

    private Boolean evaluateComparison(Object left, Object right) {
        if (left == null || right == null) {
            return handleNullComparison(left, right);
        }

        // Comparación numérica si ambos son números
        if (left instanceof Number && right instanceof Number) {
            BigDecimal leftNum = convertToNumber(left);
            BigDecimal rightNum = convertToNumber(right);
            return evaluateNumericComparison(leftNum, rightNum);
        }

        // Comparación de strings por defecto
        String leftStr = left.toString();
        String rightStr = right.toString();

        switch (operator) {
            case "=":  return leftStr.equals(rightStr);
            case "<>": return !leftStr.equals(rightStr);
            case "<":  return leftStr.compareTo(rightStr) < 0;
            case ">":  return leftStr.compareTo(rightStr) > 0;
            case "<=": return leftStr.compareTo(rightStr) <= 0;
            case ">=": return leftStr.compareTo(rightStr) >= 0;
            default: throw new UnsupportedOperationException(
                    "Operador de comparación no implementado: " + operator);
        }
    }

    private Boolean handleNullComparison(Object left, Object right) {
        switch (operator) {
            case "=":  return left == null && right == null;
            case "<>": return left != null || right != null;
            default: return null; // Otros operadores con null devuelven null
        }
    }

    private Boolean evaluateNumericComparison(BigDecimal left, BigDecimal right) {
        int comparison = left.compareTo(right);

        switch (operator) {
            case "=":  return comparison == 0;
            case "<>": return comparison != 0;
            case "<":  return comparison < 0;
            case ">":  return comparison > 0;
            case "<=": return comparison <= 0;
            case ">=": return comparison >= 0;
            default: throw new UnsupportedOperationException(
                    "Operador de comparación no implementado: " + operator);
        }
    }

    @Override
    public ExpressionPartType getType() {
        return ExpressionPartType.OPERATOR;
    }

    // Getters
    public String getOperator() {
        return operator;
    }

    public ExpressionPart getLeftOperand() {
        return leftOperand;
    }

    public ExpressionPart getRightOperand() {
        return rightOperand;
    }

    public static boolean isOperator(String token) {
        return SUPPORTED_OPERATORS.contains(token);
    }

    @Override
    public String toString() {
        return "OperatorExpression[" + operator + "](" +
                leftOperand + ", " + rightOperand + ")";
    }
}