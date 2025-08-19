package com.cfo.reporting.dto;

import com.cfo.reporting.utils.DynamicLookupProcessor;

public record ConceptDetailRecord(String codeValue, String detailLabel
        , double totCurrentBalance, Double totPreviousBalance, Double variance) {

    public static ConceptDetailRecord fromDTOs(DetailFormulaResult detailFormulaResult,
                                               DynamicLookupProcessor.Resultado resultado) {
        return new ConceptDetailRecord(
                detailFormulaResult.detailValue(),
                detailFormulaResult.detailLabel(),
                resultado.getValLookupFirst(),
                resultado.getValLookupSecond(),
                resultado.getValor()
        );


    }
    @Override
    public String toString() {
        return String.format("%s %s, %f, %f, %f", this.codeValue, this.detailLabel,this.totCurrentBalance, this.totPreviousBalance,this.variance());
    }
}