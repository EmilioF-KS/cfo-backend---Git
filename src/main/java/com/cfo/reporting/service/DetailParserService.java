package com.cfo.reporting.service;

import com.cfo.reporting.dto.CodeValueRec;
import com.cfo.reporting.dto.ColumnDetailRecord;
import com.cfo.reporting.dto.ConceptDetailRecord;
import com.cfo.reporting.dto.DetailFormulaResult;
//import com.cfo.reporting.importing.BulkRepositoryImpl;
import com.cfo.reporting.model.ConceptDetail;
import com.cfo.reporting.model.ConceptDetailValues;
import com.cfo.reporting.model.DetailFormula;
import com.cfo.reporting.repository.*;
import com.cfo.reporting.utils.DynamicLookupProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.util.*;


@Service
public class DetailParserService {

    @Autowired
    private ConceptDetailRepository conceptDetailRepository;
    @Autowired
    private DetailFormulaRepository  detailFormulaRepository;
    @Autowired
    ConceptDetailsValuesRepository conceptDetailsValuesRepository;

    public List<ConceptDetailRecord> allDetailsCalculated(String screenName,String glPeriod,
                                                          int concept_id,
                                                          Map<String,Map<String,Object>> tables ) {
        List<ConceptDetailRecord> listDetails = new ArrayList<>();
        List<ConceptDetail> allConceptDetais =
                conceptDetailRepository.allDetailsByConcept(concept_id);
        List<ColumnDetailRecord> allColumnFormulaResult = new ArrayList<>();
        //
        // Retrieve all formulas for each Details
        //
        List<DetailFormula> allFormulasByDetail=new ArrayList<>();;
        for (ConceptDetail conceptDetail: allConceptDetais) {
            //int concetDetailId =
            allFormulasByDetail = detailFormulaRepository.allFormulaDetailsById(conceptDetail.getDetail_id().intValue());
            //
            if (allFormulasByDetail == null || allFormulasByDetail.size() == 0) {
                ConceptDetailRecord conceptDetailRecord =
                        new ConceptDetailRecord(conceptDetail.getDetailLabel(),
                                allColumnFormulaResult,
                                conceptDetail.getDetail_id(),
                                conceptDetail.getDetailOrder());
                listDetails.add(conceptDetailRecord);
                return listDetails;
            }
            //
            int columnOrder = 1;
            String detailValue="";
            for (DetailFormula detailFormula : allFormulasByDetail) {
                if (conceptDetail.getDetailValue() == null || conceptDetail.getDetailValue().isEmpty() ) {
                    detailValue = detailFormula.getColumnDetailValue();
                } else {
                    detailValue = conceptDetail.getDetailValue();
                }
                if (detailValue== null) {
                    detailValue="";
                }
                //
                DynamicLookupProcessor.DynamicValue diynamicValue = new
                        DynamicLookupProcessor.DynamicValue(detailFormula.getColumnName(),
                        detailValue);
                String formula = detailFormula.getFormulaText();
                //"VLOOKUP('#{for_branch}', gldays)  -  VLOOKUP('#{for_branch}', prevgldays)";
                DynamicLookupProcessor processor = new DynamicLookupProcessor();
                CodeValueRec processorResult =
                        processor.procesar(diynamicValue, tables, formula);
                ColumnDetailRecord columnDetail = new ColumnDetailRecord(
                        detailFormula.getColumnFormulascol()
                        ,processorResult.value(),columnOrder);
                //
                 // Checks if the column has value in the tbl_cfo_column_details_values
                 //
                getColumnDetailValue(conceptDetail.getDetail_id(),concept_id,columnDetail,glPeriod);
                 //
                allColumnFormulaResult.add(columnDetail);
                columnOrder++;
            }
            ConceptDetailRecord conceptDetailRecord =
                    new ConceptDetailRecord(conceptDetail.getDetailLabel(),
                            allColumnFormulaResult,conceptDetail.getDetail_id(),
                            conceptDetail.getDetailOrder());
            listDetails.add(conceptDetailRecord);
            allColumnFormulaResult.clear();
        }

       return listDetails;
    }

    private void getColumnDetailValue(long detail_id,
                                      int concept_id,
                                      ColumnDetailRecord columnDetailRecord,
                                      String glPeriod) {

            ConceptDetailValues conceptDetailValues = conceptDetailsValuesRepository
                    .findByScreenConceptDetailAndGlPeriodAndColumn(concept_id,
                            detail_id,
                            glPeriod,
                            columnDetailRecord.getColumnName());
            if (conceptDetailValues!= null) {
                conceptDetailValues.setColumnValue(columnDetailRecord.getColumnValue());
                try {
                    conceptDetailsValuesRepository.save(conceptDetailValues);
                } catch(Exception ex) {
                    System.out.println("Exception when updating ColumnDetailvalues "+ex.getMessage());
                }
            }
    }
}
