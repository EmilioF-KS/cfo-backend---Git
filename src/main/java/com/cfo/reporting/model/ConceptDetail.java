package com.cfo.reporting.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "tbl_cfo_concept_details")
@Data
public class ConceptDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long detail_id;

    private long concept_id;
    private String detailValue;
    private String detailLabel;
    private int detailOrder;


}