package com.cfo.reporting.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "tbl_cfo_screen_concepts")
@Data
public class Concept {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long concept_id;
    private String screen_id;
    private String concept_name;
    private String concept_label;
    private int concept_order;
    private boolean is_filter;
    private long screen_concepts_concept_id;
    private String query_concepts;


}