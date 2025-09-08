package com.cfo.reporting.dto;

public record TotalByConcept(double totPreviousBalance,
                             double totCurrentBalance,
                             double totVarince) {

//    public TotalByConcept add(ConceptDetailRecord detailConcept) {
//        return new TotalByConcept(
//         totPreviousBalance + detailConcept.totCurrentBalance(),
//         totCurrentBalance + detailConcept.totPreviousBalance(),
//         totVarince + detailConcept.variance()
//        );
//    }
}
