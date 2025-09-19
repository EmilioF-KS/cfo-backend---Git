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
public class DetailFormulaKey {
    @Column(name="formula_id")
    private long conceptId;
    @Column(name="concept_detail_id")
    private long conceptDetailId;



    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        DetailFormulaKey that = (DetailFormulaKey) o;
        return conceptId == that.conceptId && conceptDetailId == that.conceptDetailId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(conceptId, conceptDetailId);
    }
}
