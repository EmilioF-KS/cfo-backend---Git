package com.cfo.reporting.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tbl_cfo_column_formulas")
@Data
public class Formula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long formula_id;
    private String formula_name;
    private String formula_text;
    private long concept_id;
    private String column_formulascol;
    private long screen_concepts_concept_id;
    private long screen_concepts_screen_concepts_concept_id;

}