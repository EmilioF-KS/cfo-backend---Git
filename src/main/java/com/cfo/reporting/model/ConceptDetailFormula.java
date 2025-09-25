package com.cfo.reporting.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "tbl_cfo_column_formulas")
@Data
public class ConceptDetailFormula {
    @EmbeddedId
    ConceptDetailFormulaKey Id;
    @Column(name="column_name")
    private String columnName;
    @Column(name="formula_text")
    private String formulaText;
    @Column(name="column_formulascol")
    private String columnFormulascol;
    @Column(name="column_detail_value")
    private String columnDetailValue;


}