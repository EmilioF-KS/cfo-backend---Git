package com.cfo.reporting.drools.vlookup;


import com.cfo.reporting.drools.vlookup.model.data.EvaluationContext;

public interface ExpressionPart {
    String getText();
    Object evaluate(EvaluationContext context);
    ExpressionPartType getType();

    enum ExpressionPartType {
        LITERAL, VARIABLE, FUNCTION, VLOOKUP, OPERATOR,SIMPLE
    }
}