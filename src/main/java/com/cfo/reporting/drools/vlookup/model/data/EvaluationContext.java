package com.cfo.reporting.drools.vlookup.model.data;

import java.util.Map;
import java.util.function.Function;

public interface EvaluationContext {
    Object resolveVariable(String name);
    void setVariable(String name, Object value);
    Map<String, Object> getVariables();
    void registerFunction(String name, Function<Object[], Object> function);
    Object get(String key);


}