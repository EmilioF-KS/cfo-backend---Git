package com.cfo.reporting.dto;

import com.cfo.reporting.model.ConceptDetail;
import com.cfo.reporting.model.Screen;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ConceptDTO {
    private Long id;
    private Screen screen;
    private String conceptName;
    private String conceptLabel;
    private int conceptOrder;
    private boolean isFilter;
    private List<ConceptDetail> detalles;
}