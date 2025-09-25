package com.cfo.reporting.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class ConceptDetailValuesDTO {
    private long conceptId;
    private long conceptDetailId;
    private String columnName;
    private double columnValue;
    private String glPeriod;

    public ConceptDetailValuesDTO() {
    }



    public long getConceptId() {
        return conceptId;
    }

    public ConceptDetailValuesDTO setConceptId(long conceptId) {
        this.conceptId = conceptId;
        return this;
    }

    public long getConceptDetailId() {
        return conceptDetailId;
    }

    public ConceptDetailValuesDTO setConceptDetailId(long conceptDetailId) {
        this.conceptDetailId = conceptDetailId;
        return this;
    }

    public String getColumnName() {
        return columnName;
    }

    public ConceptDetailValuesDTO setColumnName(String columnName) {
        this.columnName = columnName;
        return this;
    }

    public double getColumnValue() {
        return columnValue;
    }

    public ConceptDetailValuesDTO setColumnValue(double columnValue) {
        this.columnValue = columnValue;
        return this;
    }

    public String getGlPeriod() {
        return glPeriod;
    }

    public ConceptDetailValuesDTO setGlPeriod(String glPeriod) {
        this.glPeriod = glPeriod;
        return this;
    }
}
