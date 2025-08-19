package com.cfo.reporting.drools.vlookup;


import com.cfo.reporting.drools.vlookup.model.data.EvaluationContext;

public class SimpleExpression implements ExpressionPart {

    private final String text;

    public SimpleExpression(String text) {
        this.text = text;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public Object evaluate(EvaluationContext context) {
        // If text starts with "$", treat it as a variable from context
        if (text.startsWith("$")) {
            return context.get(text.substring(1)); // remove $ and get value
        }
        // Otherwise, treat as literal
        return text;
    }

    @Override
    public ExpressionPartType getType() {
        return ExpressionPartType.SIMPLE;
    }
}