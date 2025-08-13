package com.cfo.reporting.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tbl_cfo_column_formulas")
public class Formula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String expresion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "detalle_concepto_id", nullable = false)
    private ConceptDetail detalleConcepto;

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getExpresion() { return expresion; }
    public void setExpresion(String expresion) { this.expresion = expresion; }
    public ConceptDetail getDetalleConcepto() { return detalleConcepto; }
    public void setDetalleConcepto(ConceptDetail detalleConcepto) { this.detalleConcepto = detalleConcepto; }
}