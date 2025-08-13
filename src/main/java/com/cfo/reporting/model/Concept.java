package com.cfo.reporting.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "tbl_cfo_screen_concepts")
public class Concept {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screen_id", nullable = false)
    private Screen screen;

    // Relación 1:N con DetalleConcepto
    @OneToMany(mappedBy = "concepto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConceptDetail> detalles;

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Screen getScreen() { return screen; }
    public void setScreen(Screen screen) { this.screen = screen; }
    public List<ConceptDetail> getDetalles() { return detalles; }
    public void setDetalles(List<ConceptDetail> detalles) { this.detalles = detalles; }
}