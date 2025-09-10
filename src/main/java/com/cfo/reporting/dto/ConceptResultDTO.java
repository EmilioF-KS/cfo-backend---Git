package com.cfo.reporting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConceptResultDTO {
    private long conceptId;
    private String descripcion;
    private int conceptOrder;
    private boolean isFilter;
    List<ColumnDetailRecord> allColumns = new ArrayList<>();
    private List<ConceptDetailRecord> detalles = new ArrayList<>();


}
