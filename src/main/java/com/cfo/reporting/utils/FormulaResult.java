package com.cfo.reporting.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class FormulaResult {
    private Long formulaId;
    private String columnName;
    private Object result;
    private List<ExecutionDetail> details;

    public FormulaResult() {

    }
}
