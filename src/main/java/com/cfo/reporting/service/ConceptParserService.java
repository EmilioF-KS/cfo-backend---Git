package com.cfo.reporting.service;

import com.cfo.reporting.dto.ColumnDetailRecord;
import com.cfo.reporting.dto.ConceptDetailRecord;
import com.cfo.reporting.dto.ConceptResultDTO;
import com.cfo.reporting.importing.BulkRepositoryImpl;
import com.cfo.reporting.model.Concept;
import com.cfo.reporting.model.ConceptDetailValueKey;
import com.cfo.reporting.model.ConceptDetailValues;
import com.cfo.reporting.repository.ConceptDetailsValuesRepository;
import com.cfo.reporting.repository.ConceptRepository;
import com.cfo.reporting.service.config.ConsultaConfig;
import com.cfo.reporting.service.config.PantallaConfig;
import com.cfo.reporting.service.config.PantallaConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.cfo.reporting.service.config.PantallaService;

import java.util.*;
import java.util.stream.Collectors;

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

    @Autowired
    ConceptDetailsValuesRepository conceptDetailsValuesRepository;

    public Map<String,Object> allConceptsScreen(String screenId, String glPeriod, Pageable page,int pageNumber,int pageSize) {
        Map<String,Object> allResultsConcepts = new HashMap<>();
        Map<String,Object> pageData = new HashMap<>();

        try {
            if (hasSubconcepts(screenId)) {
                List<?> allRetrievedRecords = allConceptsWithSubconcepts(
                        screenId, glPeriod, getTablesData(screenId, glPeriod)
                );

                long total = allRetrievedRecords.size();
                int offset = pageNumber * pageSize;
                boolean hasNext = ((pageNumber + 1) * pageSize) < total; //
                boolean hasPrev = ((pageNumber)*pageSize > 0); // 30>0
                int totalPages  = (int) Math.ceil(total / (double) pageSize);

                pageData.put("totalPages", totalPages);
                pageData.put("totalItems", total);
                pageData.put("pageNumber", pageNumber);
                pageData.put("hasPreviousPage", hasPrev);
                pageData.put("hasNextPage", hasNext);

                int from = Math.min(offset, allRetrievedRecords.size());
                int to = Math.min(from + pageSize, allRetrievedRecords.size());
                List<?> slice = allRetrievedRecords.subList(from, to);

                allResultsConcepts.put("allConcepts", slice);
                allResultsConcepts.put("pageData", pageData);
            } else {
                allResultsConcepts = allConceptsWithoutSubconcepts(screenId, glPeriod, page);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            pageData.put("offsetPage", 0);
            pageData.put("totalPages", 0);
            pageData.put("totalItems", 0L);
            pageData.put("pageNumber", page.getPageNumber());
            pageData.put("hasPreviousPage", false);
            pageData.put("hasNextPage", false);
            allResultsConcepts.put("allConcepts", Collections.emptyList());
            allResultsConcepts.put("pageData", pageData);
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
        List<ColumnDetailRecord> conceptColums = allDetailsByConcept
                .stream().flatMap(conceptDetail->
                        conceptDetail.allColumns().stream())
                .collect(Collectors.groupingBy(
                        ColumnDetailRecord::getColumnName,
                        Collectors.summingDouble(ColumnDetailRecord::getColumnValue)))
                .entrySet().stream()
                .map(entry-> new ColumnDetailRecord(entry.getKey(),entry.getValue(),0))
                .collect(Collectors.toList());
        conceptResultDTO.setAllColumns(conceptColums);

        return conceptResultDTO;
    }

    private Map<String,Object> allConceptsWithoutSubconcepts(String screenId, String glPeriod, Pageable page) {
        List<Concept> allConcepts = new ArrayList<>();
        List<ConceptResultDTO> allResultsConcepts = new ArrayList<>();
        Map<String,Object> mapConceptResults = new HashMap<>();
        Map<String,Object> pageData = new HashMap<>();
        allConcepts = conceptRepository.allConceptsByScreenId(screenId);
        for(Concept concept : allConcepts)  {
            if (concept.getQuery_concepts() != null && concept.getQuery_concepts().toLowerCase().contains("select")) {
                List<Map<String,Object>> listDetails = new ArrayList<>();
                pageData = getPageableData(concept.getQuery_concepts().toLowerCase(),page);
                listDetails = dynamicQueryService.executeDynamicQuery(
                        concept.getQuery_concepts()+" where gl_period ='"+glPeriod+"' LIMIT "+
                                 page.getPageSize() +
                                " OFFSET "+pageData.get("offsetPage"),concept.getConcept_label());
                mapConceptResults.put("allConcepts",listDetails);
                mapConceptResults.put("pageData",pageData);
                return mapConceptResults;
            }
        }
        mapConceptResults.put("allConcepts",allConcepts);
        mapConceptResults.put("pageData",pageData);
        return mapConceptResults;
    }

    private List<?> allConceptsWithSubconcepts(String screenId, String glPeriod,
                                               Map<String, Map<String, Object>> tablesData) throws Exception {
        List<Concept> allParentConcepts = conceptRepository.allParentConcepts(screenId);
        List<ConceptResultDTO> allResultsConcepts = new ArrayList<>();

        for (Concept concept : allParentConcepts) {
            List<ConceptResultDTO> resultsParentSubConcepts = new ArrayList<>();

            List<Concept> allSubconcepts =
                    conceptRepository.allSubConceptsByConceptId(screenId,(int)concept.getConcept_id());
            for (Concept subconcept : allSubconcepts) {
                resultsParentSubConcepts.add(conceptWithDetails
                        (screenId, glPeriod, (int) subconcept.getConcept_id(),tablesData));
            }
            // get total by Parent
            ConceptResultDTO parentConceptDTO;
            if (!resultsParentSubConcepts.isEmpty()) {
                parentConceptDTO = addingSubconcepts(concept, resultsParentSubConcepts);
            } else {
                parentConceptDTO = new ConceptResultDTO();
                parentConceptDTO.setConceptId(concept.getConcept_id());
                parentConceptDTO.setConceptOrder(concept.getConcept_order());
                parentConceptDTO.setDescripcion(concept.getConcept_name());
                parentConceptDTO.setFilter(false);
            }

            allResultsConcepts.add(parentConceptDTO);
            allResultsConcepts.addAll(resultsParentSubConcepts);
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
        List<ColumnDetailRecord> conceptColums = allConcepts
                .stream().flatMap(conceptDetail->
                        conceptDetail.getAllColumns().stream())
                .collect(Collectors.groupingBy(
                        ColumnDetailRecord::getColumnName,
                        Collectors.summingDouble(ColumnDetailRecord::getColumnValue)))
                .entrySet().stream()
                .map(entry-> new ColumnDetailRecord(entry.getKey(),entry.getValue(),0))
                .collect(Collectors.toList());
        conceptResultDTO.setAllColumns(conceptColums);

        return conceptResultDTO;

    }
    private boolean hasSubconcepts(String screenId) {
        String queryForValidatingSubconcepts = ApplicationConstants.queryHasSubconcepts +
                " and screen_id = '"+screenId+"'";
        long subConceptsFound = this.bulkRepository.recordsProcessedByTable(queryForValidatingSubconcepts);
        System.out.println("Total subConcepts: "+subConceptsFound);
        return  subConceptsFound > 0;
    }


    private Map<String,Map<String,Object>> getTablesData(String screenId, String glPeriod) {
        Map<String,Map<String,Object>> tableValues = new HashMap<>();
        Map<String, Object> parametros = new HashMap<>();
        PantallaConfig pantallaConfig = null;
       try {
           pantallaConfig = pantallaConfigRepository.obtenerConfiguracion(screenId);
           List<ConsultaConfig> listParameters = pantallaConfig.getConsultas();
           switch (screenId) {
               case "scr_worksheet":
                   parametros.put(String.valueOf(
                           listParameters.get(0).getParametros().get(0)), glPeriod);
                   parametros.put(String.valueOf(
                                   listParameters.get(1).getParametros().get(0)),
                           SubtractMonth.subtractOneMonth(glPeriod));
                   break;
               default:
                   for (ConsultaConfig consultaConfig : listParameters) {
                       String keyParameter = String.valueOf(consultaConfig.getParametros().get(0).getNombre());
                       parametros.put(keyParameter, glPeriod);
                   }
                   break;
           }
           tableValues =  pantallaService.obtenerTodasConsultasPantalla(screenId,parametros);
       } catch(Exception ex) {
           System.out.println("Not tables configuration found");
       }
       return tableValues;
    }


    private Map<String,Object> getPageableData(String QuerytoExecute, Pageable pageable) {
        long totRows = bulkRepository.recordsProcessedByTable(QuerytoExecute.replaceAll("(?i)select\\s+\\*","select count(*) "));
        int currOffset =0;
        boolean nextPage;
        int totPages = (int) Math.ceil((double) totRows/ pageable.getPageSize());
        Map<String,Object> pageData = new HashMap<>();
        if (pageable.getPageNumber() == 0) {
            currOffset=pageable.getPageSize();
        }
        else {
            currOffset = pageable.getPageNumber() * pageable.getPageSize();
        }

        //Checking if next page is inside the total amount of rows
        nextPage = ((pageable.getPageNumber() + 1) * pageable.getPageSize()) < totRows;

        pageData.put("offsetPage", currOffset);
        pageData.put("totalPages",totPages);
        pageData.put("totalItems",totRows);
        pageData.put("pageNumber",pageable.getPageNumber());
        pageData.put("hasPreviousPage",pageable.hasPrevious());
        pageData.put("hasNextPage",nextPage);
        return pageData;
    }

    private boolean saveConceptDetailValues(List<ConceptResultDTO> listValues) {
//        try {
//            listValues.forEach(conceptParent -> {
//                 ConceptDetailValueKey conceptDetailValueKey = new ConceptDetailValueKey();
//                conceptDetailValueKey.setConceptDetailId();
//                 ConceptDetailValues conceptDetailValues = new ConceptDetailValues();
//                 conceptDetailsValuesRepository.save()
//            });
//
//        }
//        catch (Exception ex) {
//
//        }
        return true;
    }

}
