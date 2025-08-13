package com.cfo.reporting.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "tbl_cfo_concept_details")
public class ConceptDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concepto_id", nullable = false)
    private Concept concepto;

    // Relación 1:N con Formula
    @OneToMany(mappedBy = "detalleConcepto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Formula> formulas;

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Concept getConcepto() { return concepto; }
    public void setConcepto(Concept concepto) { this.concepto = concepto; }
    public List<Formula> getFormulas() { return formulas; }
    public void setFormulas(List<Formula> formulas) { this.formulas = formulas; }
}