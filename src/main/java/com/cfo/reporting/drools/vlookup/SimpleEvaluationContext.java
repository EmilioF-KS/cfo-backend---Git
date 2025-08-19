package com.cfo.reporting.drools.vlookup;


import com.cfo.reporting.drools.vlookup.model.data.EvaluationContext;

import java.util.Map;
import java.util.function.Function;

public class SimpleEvaluationContext implements EvaluationContext {
    private final Map<String, Object> variables;

    public SimpleEvaluationContext(Map<String, Object> variables) {
        this.variables = variables;
    }

    @Override
    public Object resolveVariable(String name) {
        return variables.get(name);
    }

    @Override
    public void setVariable(String name, Object value) {
        variables.put(name, value);
    }

    @Override
    public Map<String, Object> getVariables() {
        return variables;
    }

    @Override
    public void registerFunction(String name, Function<Object[], Object> function) {
        throw new UnsupportedOperationException();
    }

    // Added so ctx.get("...") works
    @Override
    public Object get(String key) {
        return variables.get(key);
    }

    public Map<String, Object> getValues() {
        return variables;
    }
}
