package com.cfo.reporting.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tbl_cfo_column_formulas_details")
@Data
public class DetailFormula {
    @EmbeddedId
    DetailFormulaKey Id;
    @Column(name="column_name")
    private String columnName;
    @Column(name="formula_text")
    private String formulaText;
    @Column(name="column_formulascol")
    private String columnFormulascol;
    @Column(name="column_detail_value")
    private String columnDetailValue;


}