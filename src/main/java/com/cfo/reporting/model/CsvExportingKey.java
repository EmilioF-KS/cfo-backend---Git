package com.cfo.reporting.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class CsvExportingKey {
    @Column(name="gl_period")
    private String glPeriod;
    @Column(name="indicator_id")
    private String indicatorId;
    @Column(name="form_id")
    private String formId;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CsvExportingKey that = (CsvExportingKey) o;
        return Objects.equals(glPeriod, that.glPeriod) && Objects.equals(indicatorId, that.indicatorId) && Objects.equals(formId, that.formId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(glPeriod, indicatorId, formId);
    }

    @Override
    public String toString() {
        return "CsvExportingKey{" +
                "glPeriod='" + glPeriod + '\'' +
                ", indicatorId='" + indicatorId + '\'' +
                ", formId='" + formId + '\'' +
                '}';
    }
}
