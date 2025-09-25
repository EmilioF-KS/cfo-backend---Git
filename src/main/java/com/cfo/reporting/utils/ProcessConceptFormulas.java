package com.cfo.reporting.utils;

import com.cfo.reporting.dto.CodeValueRec;
import com.cfo.reporting.dto.ColumnDetailRecord;
import com.cfo.reporting.dto.ConceptDetailRecord;
import com.cfo.reporting.dto.ConceptResultDTO;
import com.cfo.reporting.model.ConceptDetail;
import com.cfo.reporting.model.ConceptDetailFormula;
import com.cfo.reporting.model.DetailFormula;
import com.cfo.reporting.repository.ConceptDetailFormulaRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
@NoArgsConstructor
public class ProcessConceptFormulas {

    @Autowired
    ConceptDetailFormulaRepository conceptDetailFormulaRepository;

    public ConceptResultDTO processConceptFormulas(ConceptResultDTO conceptResultDTO, String glPeriod,
                                                   Map<String, Map<String, Object>> tablesData) {
        List<ConceptDetailFormula> allFormulasByConcept = new ArrayList<>();
        List<ColumnDetailRecord> allColumnDetailRecord = new ArrayList<>();


            allFormulasByConcept = conceptDetailFormulaRepository
                    .allFormulaDetailsByConceptId(conceptResultDTO.getConceptId());
            //

            if (allFormulasByConcept == null || allFormulasByConcept.size() == 0) {
                conceptResultDTO.setAllColumns(allColumnDetailRecord);
                return conceptResultDTO;
            }
            //
            int columnOrder = 1;
            String detailValue = "";
            for (ConceptDetailFormula conceptDetailFormula : allFormulasByConcept) {
                //
                detailValue = conceptDetailFormula.getColumnDetailValue();
                DynamicLookupProcessor.DynamicValue diynamicValue = new
                        DynamicLookupProcessor.DynamicValue(conceptDetailFormula.getColumnName(),
                        detailValue);
                String formula = conceptDetailFormula.getFormulaText();
                //"VLOOKUP('#{for_branch}', gldays)  -  VLOOKUP('#{for_branch}', prevgldays)";
                DynamicLookupProcessor processor = new DynamicLookupProcessor();
                CodeValueRec processorResult =
                        processor.procesar(diynamicValue, tablesData, formula);
                ColumnDetailRecord columnDetail = new ColumnDetailRecord(
                        conceptDetailFormula.getColumnFormulascol()
                        , processorResult.value(), columnOrder);
                //
                // Checks if the column has value in the tbl_cfo_column_details_values
                //
                // getColumnDetailValue(conceptDetail.getDetail_id(), concept_id, columnDetail, glPeriod);
                //
                allColumnDetailRecord.add(columnDetail);
                columnOrder++;
            }
        conceptResultDTO.setAllColumns(allColumnDetailRecord);
        return conceptResultDTO;
    }

}
