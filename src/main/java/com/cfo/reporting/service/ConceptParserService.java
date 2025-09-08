package com.cfo.reporting.service;

import com.cfo.reporting.dto.ConceptDetailRecord;
import com.cfo.reporting.dto.ConceptResultDTO;
import com.cfo.reporting.importing.BulkRepositoryImpl;
import com.cfo.reporting.model.Concept;
import com.cfo.reporting.repository.ConceptRepository;
import com.cfo.reporting.service.config.ConsultaConfig;
import com.cfo.reporting.service.config.PantallaConfig;
import com.cfo.reporting.service.config.PantallaConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.cfo.reporting.service.config.PantallaService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.cfo.reporting.config.ApplicationConstants;
import com.cfo.reporting.utils.SubtractMonth;

@Service
public class ConceptParserService {

    @Autowired
    private ConceptRepository conceptRepository;
    @Autowired
    DetailParserService detailParserService;

    @Autowired
    DynamicQueryService dynamicQueryService;

    @Autowired
    PantallaService pantallaService;
    @Autowired
    PantallaConfigRepository pantallaConfigRepository;

    @Autowired
    private BulkRepositoryImpl bulkRepository;

    public List<?> allConceptsScreen(String screenId, String glPeriod) {
        //
         // Validates if the concept has a Query to execute
        List<?> allResultsConcepts = new ArrayList<>();
        //
        try {
            if (hasSubconcepts(screenId)) {
                allResultsConcepts = allConceptsWithSubconcepts(screenId,
                        glPeriod,getTablesData(screenId,glPeriod));
            } else {
                allResultsConcepts = allConceptsWithoutSubconcepts(screenId, glPeriod);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
       return allResultsConcepts;
    }

    private ConceptResultDTO conceptWithDetails(String screenId, String glPeriod,int conceptId,Map<String,Map<String,Object>> tablesData) {
        List<ConceptDetailRecord> allDetailsByConcept =
                detailParserService.allDetailsCalculated(screenId,glPeriod,conceptId,tablesData);
       Concept concept= conceptRepository.allConceptsByScreenIdConceptId(screenId,conceptId);
        //
        ConceptResultDTO conceptResultDTO  = new ConceptResultDTO();
        conceptResultDTO.setConceptId(concept.getConcept_id());
        conceptResultDTO.setConceptOrder(concept.getConcept_order());
        conceptResultDTO.setDescripcion(concept.getConcept_name());
        conceptResultDTO.setFilter(concept.is_filter());
        conceptResultDTO.setDetalles(allDetailsByConcept);

//        double totCurrentBalance = allDetailsByConcept
//                .stream()
//                .mapToDouble(ConceptDetailRecord::totCurrentBalance).sum();
//        double totPreviousBalance = allDetailsByConcept
//                .stream()
//                .mapToDouble(ConceptDetailRecord::totPreviousBalance).sum();
//        conceptResultDTO.setDetalles(allDetailsByConcept);
//
//        double totalVariance = allDetailsByConcept
//                .stream()
//                .mapToDouble(ConceptDetailRecord::variance).sum();
//        conceptResultDTO.setTotBalanceCurrent(totCurrentBalance);
//        conceptResultDTO.setTotBalancePrevious(totPreviousBalance);
//        conceptResultDTO.setTotVariance(totalVariance);

        return conceptResultDTO;
    }

    private List<?> allConceptsWithoutSubconcepts(String screenId, String glPeriod) {
        List<Concept> allConcepts = new ArrayList<>();
        List<ConceptResultDTO> allResultsConcepts = new ArrayList<>();
        allConcepts = conceptRepository.allConceptsByScreenId(screenId);
        for(Concept concept : allConcepts)  {
            if (concept.getQuery_concepts() != null && concept.getQuery_concepts().toLowerCase().contains("select")) {
                List<Map<String,Object>> listDetails = new ArrayList<>();
                listDetails = dynamicQueryService.executeDynamicQuery(
                        concept.getQuery_concepts()+" where gl_period ='"+glPeriod+"'",concept.getConcept_label());
                return listDetails;
            }
        }

        return allConcepts;
    }

    private List<?> allConceptsWithSubconcepts(String screenId, String glPeriod,
                                               Map<String, Map<String, Object>> tablesData) throws Exception{
        List<Concept> allConceptsReults = new ArrayList<>();
        List<Concept> allParentConcepts = conceptRepository.allParentConcepts(screenId);
        List<ConceptResultDTO> allResultsConcepts = new ArrayList<>();
        List<ConceptResultDTO> resultsParentSubConcepts = new ArrayList<>();
        ConceptResultDTO parentConceptDTO = new ConceptResultDTO();
        for (Concept concept : allParentConcepts ) {
            List<Concept> allSubconcepts =
                    conceptRepository.allSubConceptsByConceptId(screenId,(int)concept.getConcept_id());
            for (Concept subconcept : allSubconcepts) {
                resultsParentSubConcepts.add(conceptWithDetails
                        (screenId, glPeriod, (int) subconcept.getConcept_id(),tablesData));
            }
            // get total by Parent
            if (resultsParentSubConcepts.size() > 0 ) {
                parentConceptDTO = addingSubconcepts(concept,resultsParentSubConcepts);
            }
            else {
                parentConceptDTO.setConceptId(concept.getConcept_id());
                parentConceptDTO.setConceptOrder(concept.getConcept_order());
                parentConceptDTO.setDescripcion(concept.getConcept_name());
                parentConceptDTO.setTotBalanceCurrent(0);
                parentConceptDTO.setTotVariance(0);
                parentConceptDTO.setTotBalancePrevious(0);
                parentConceptDTO.setFilter(false);
            }
            allResultsConcepts.add(parentConceptDTO);
            allResultsConcepts.addAll(resultsParentSubConcepts);
            System.out.println("Current REsults "+resultsParentSubConcepts.size());
        }
        return allResultsConcepts;
    }


    private ConceptResultDTO addingSubconcepts(Concept parentConcept,List<ConceptResultDTO> allConcepts) {
        //
        ConceptResultDTO conceptResultDTO  = new ConceptResultDTO();
        conceptResultDTO.setConceptId(parentConcept.getConcept_id());
        conceptResultDTO.setConceptOrder(parentConcept.getConcept_order());
        conceptResultDTO.setDescripcion(parentConcept.getConcept_name());
        conceptResultDTO.setFilter(parentConcept.is_filter());

        double totCurrentBalance = allConcepts
                .stream()
                .mapToDouble(ConceptResultDTO::getTotBalanceCurrent).sum();
        double totPreviousBalance = allConcepts
                .stream()
                .mapToDouble(ConceptResultDTO::getTotBalancePrevious).sum();

        double totalVariance = allConcepts
                .stream()
                .mapToDouble(ConceptResultDTO::getTotVariance).sum();
        conceptResultDTO.setTotBalanceCurrent(totCurrentBalance);
        conceptResultDTO.setTotBalancePrevious(totPreviousBalance);
        conceptResultDTO.setTotVariance(totalVariance);

        return conceptResultDTO;

    }
    private boolean hasSubconcepts(String screenId) {
        String queryForValidatingSubconcepts = ApplicationConstants.queryHasSubconcepts +
                " and screen_id = '"+screenId+"'";
        return this.bulkRepository.recordsProcessedByTable(queryForValidatingSubconcepts) > 0;
    }


    private Map<String,Map<String,Object>> getTablesData(String screenId, String glPeriod) {
       Map<String, Object> parametros = new HashMap<>();
       PantallaConfig pantallaConfig = pantallaConfigRepository.obtenerConfiguracion(screenId);
       List<ConsultaConfig> listParameters = pantallaConfig.getConsultas();

       switch (screenId) {
           case "scr_worksheet" :
               parametros.put(String.valueOf(
                       listParameters.get(0).getParametros().get(0)),glPeriod);
               parametros.put(String.valueOf(
                       listParameters.get(1).getParametros().get(0)),
                       SubtractMonth.subtractOneMonth(glPeriod));
               break;
           default:
               for (ConsultaConfig consultaConfig: listParameters ) {
                   String keyParameter = String.valueOf(consultaConfig.getParametros().get(0).getNombre());
                   parametros.put(keyParameter,glPeriod);
               }
               break;
       }
       return pantallaService.obtenerTodasConsultasPantalla(screenId,parametros);
    }


}
