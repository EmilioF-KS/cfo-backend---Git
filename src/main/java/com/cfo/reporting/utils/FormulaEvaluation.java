package com.cfo.reporting.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FormulaEvaluation {
    private String formula;
    private Map<String, Object> context;
    private Object result;
    private List<ExecutionDetail> executionDetails = new ArrayList<>();

    public FormulaEvaluation(String formulaText, Map<String, Object> context) {

    }

    // Constructor, getters y setters

    public void addDetail(String step, Object value, String message) {
        executionDetails.add(new ExecutionDetail(step, value, message));
    }
}
