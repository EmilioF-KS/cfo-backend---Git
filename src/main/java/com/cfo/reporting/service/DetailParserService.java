package com.cfo.reporting.service;

import com.cfo.reporting.dto.ConceptDetailRecord;
import com.cfo.reporting.dto.DetailFormulaResult;
import com.cfo.reporting.importing.BulkRepositoryImpl;
import com.cfo.reporting.repository.ConceptDetailRepository;
import com.cfo.reporting.repository.ConceptRepository;
import com.cfo.reporting.repository.ScreenRepository;
import com.cfo.reporting.utils.CommonsListCombiner;
import com.cfo.reporting.utils.DynamicLookupProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DetailParserService {
    @Autowired
    private BulkRepositoryImpl bulkRepositoryImpl;
    @Autowired
    private ConceptRepository conceptRepository;
    @Autowired
    private ScreenRepository screenRepository;
    @Autowired
    private ConceptDetailRepository conceptDetailRepository;
    @Autowired
    private BulkRepositoryImpl bulkRepository;



    public List<ConceptDetailRecord> allDetailsCalculated(String screenName,String glPeriod,int concept_id) {

        String sqlGLDAYSCurrent = "Select for_branch as id,tot_current_balance as value from " +
                "tbl_cfo_gldays where gl_period = '"+glPeriod+"'";
        String sqlGLDAYSPrevious = "Select for_branch as id,tot_current_balance as value from " +
                "tbl_cfo_gldays where gl_period = '202502'";
        Map<String,Map<String,Object>> tables = new HashMap<>();
        Map<String,Object> currentValues = bulkRepositoryImpl.valuesForQuery(sqlGLDAYSCurrent);
        Map<String,Object> previousValues = bulkRepositoryImpl.valuesForQuery(sqlGLDAYSPrevious);
        //
        tables.put("gldays",currentValues);
        tables.put("prevgldays",previousValues);
        //
        List<DetailFormulaResult> allDetailFormulaRes =
                conceptDetailRepository.allDetailsByScreenAndConcept(screenName,concept_id);
        //
        List<DynamicLookupProcessor.DynamicValue> allResultados  = allDetailFormulaRes
                .stream()
                .map(result-> {
                    DynamicLookupProcessor.DynamicValue dynamicLookupProcessor = new DynamicLookupProcessor.DynamicValue(result.columnName(),
                           result.detailValue());
                   return dynamicLookupProcessor;
                } )
                .collect(Collectors.toList());


        String formula = allDetailFormulaRes.get(0).formulaText();
                //"VLOOKUP('#{for_branch}', gldays)  -  VLOOKUP('#{for_branch}', prevgldays)";

        DynamicLookupProcessor processor = new DynamicLookupProcessor();
        List<DynamicLookupProcessor.Resultado>
                allProcessorResul = processor.procesar(allResultados,tables,formula);


        List<ConceptDetailRecord> detailsList = CommonsListCombiner.combineLists(
                allDetailFormulaRes,
                allProcessorResul,
                ConceptDetailRecord::fromDTOs
        );

       return detailsList;
    }

}
