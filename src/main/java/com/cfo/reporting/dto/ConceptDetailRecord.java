package com.cfo.reporting.dto;

import com.cfo.reporting.utils.DynamicLookupProcessor;

import java.util.ArrayList;
import java.util.List;

public record ConceptDetailRecord(String detailLabel
        , List<ColumnDetailRecord> allColumns,long detailId, int detilOrder) {

    public ConceptDetailRecord {
        allColumns = new ArrayList<>(allColumns);
    }

    public static ConceptDetailRecord fromDTOs(String detailLabel,
                                               List<ColumnDetailRecord> allColumns,
                                               long detailId,
                                               int detilOrder) {
        return new ConceptDetailRecord(
                detailLabel,
                allColumns,
                detailId,
                detilOrder

        );


    }

}