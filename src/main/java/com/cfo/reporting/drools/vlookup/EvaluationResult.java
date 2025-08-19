package com.cfo.reporting.drools.vlookup;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class EvaluationResult {
    private Object value;
    private List<String> warnings = new ArrayList<>();
    private List<String> errors = new ArrayList<>();
    private Map<String, Object> debugInfo = new HashMap<>();

    public EvaluationResult() {

    }

    // Getters y setters
    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public void addWarning(String warning) {
        warnings.add(warning);
    }

    public void addError(String error) {
        errors.add(error);
    }

    public void addDebugInfo(String key, Object value) {
        debugInfo.put(key, value);
    }

    public List<String> getErrors() { return errors; }
    public Object getValue() { return value; }
}