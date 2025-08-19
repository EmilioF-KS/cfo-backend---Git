package com.cfo.reporting.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExecutionDetail {
    private String step;
    private Object value;
    private String message;
    // Getters y setters
}