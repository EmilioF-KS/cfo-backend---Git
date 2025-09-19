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
public class ConceptDetailValuesKey {
    @Column(name="concept_detail_id")
    private long conceptDetailId;
    @Column(name="concept_id")
    private long conceptId;
    @Column(name="gl_period")
    private String glPeriod;
    @Column(name="column_name")
    private String columnName;




    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ConceptDetailValuesKey that = (ConceptDetailValuesKey) o;
        return conceptId == that.conceptId && conceptDetailId == that.conceptDetailId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(conceptId, conceptDetailId);
    }

    @Override
    public String toString() {
        return "ConceptDetailValuesKey{" +
                "conceptDetailId=" + conceptDetailId +
                ", conceptId=" + conceptId +
                ", glPeriod='" + glPeriod + '\'' +
                '}';
    }
}
