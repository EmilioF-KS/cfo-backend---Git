package com.cfo.reporting.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class ConceptDetailValueKey {
    @Column(name="concept_detail_id")
    private long conceptDetailId;
    @Column(name="concept_id")
    private long conceptId;

    @Column(name="gl_period")
    private String glPeriod;


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ConceptDetailValueKey that = (ConceptDetailValueKey) o;
        return conceptDetailId == that.conceptDetailId && conceptId == that.conceptId && Objects.equals(glPeriod, that.glPeriod);
    }

    @Override
    public int hashCode() {
        return Objects.hash(conceptDetailId, conceptId, glPeriod);
    }
}
