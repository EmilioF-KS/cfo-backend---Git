package com.cfo.reporting.service;

import com.cfo.reporting.dto.ConceptDTO;
import com.cfo.reporting.dto.ConceptDetailRecord;
import com.cfo.reporting.dto.ConceptResultDTO;
import com.cfo.reporting.dto.TotalByConcept;
import com.cfo.reporting.importing.BulkRepositoryImpl;
import com.cfo.reporting.model.Concept;
import com.cfo.reporting.model.ConceptDetail;
import com.cfo.reporting.repository.ConceptRepository;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class ConceptParserService {
    @Autowired
    private BulkRepositoryImpl bulkRepositoryImpl;
    @Autowired
    private ConceptRepository conceptRepository;
    @Autowired
    DetailParserService detailParserService;

    @Autowired
    DynamicQueryService dynamicQueryService;

    @Autowired
    private BulkRepositoryImpl bulkRepository;

    public List<?> allConceptsScreen(String screenId, String glPeriod) {
        //
         // Validates if the concept has a Query to execute
         //
        List<Concept> allConcepts = conceptRepository.allConceptsByScreenId(screenId);
        List<ConceptResultDTO> allResultsConcepts = new ArrayList<>();
        for(Concept concept : allConcepts)  {
            if (concept.getQuery_concepts().toLowerCase().contains("select")) {
                List<Map<String,Object>> listDetails = new ArrayList<>();
                listDetails = dynamicQueryService.executeDynamicQuery(
                        concept.getQuery_concepts()+" where gl_period ='"+glPeriod+"'","tbl_cfo_ddalst");

               return listDetails;
            } else {
                allResultsConcepts.add(conceptWithDetails
                        (screenId, glPeriod, (int) concept.getConcept_id()));
            }
        }
        return allResultsConcepts;
    }

    public ConceptResultDTO conceptWithDetails(String screenId, String glPeriod,int conceptId) {


        List<ConceptDetailRecord> allDetailsByConcept =
                detailParserService.allDetailsCalculated(screenId,glPeriod,conceptId);


        Concept concept= conceptRepository.allConceptsByScreenIdConceptId(screenId,conceptId);

//
        System.out.println("Sumatoria para el concept "+screenId+" "+concept.getConcept_id()+" total "+
                allDetailsByConcept
                        .stream()
                        .mapToDouble(ConceptDetailRecord::totPreviousBalance).sum());

        //
        ConceptResultDTO conceptResultDTO  = new ConceptResultDTO();
        conceptResultDTO.setConceptId(concept.getConcept_id());
        conceptResultDTO.setConceptOrder(concept.getConcept_order());
        conceptResultDTO.setDescripcion(concept.getConcept_name());
        conceptResultDTO.setFilter(concept.is_filter());

        double totCurrentBalance = allDetailsByConcept
                .stream()
                .mapToDouble(ConceptDetailRecord::totCurrentBalance).sum();
        double totPreviousBalance = allDetailsByConcept
                .stream()
                .mapToDouble(ConceptDetailRecord::totPreviousBalance).sum();
        conceptResultDTO.setDetalles(allDetailsByConcept);

        double totalVariance = allDetailsByConcept
                .stream()
                .mapToDouble(ConceptDetailRecord::variance).sum();
        conceptResultDTO.setTotBalanceCurrent(totCurrentBalance);
        conceptResultDTO.setTotBalancePrevious(totPreviousBalance);
        conceptResultDTO.setTotVariance(totalVariance);

        return conceptResultDTO;
    }

}
