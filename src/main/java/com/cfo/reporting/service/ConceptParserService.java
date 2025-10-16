package com.cfo.reporting.service;

import com.cfo.reporting.dto.ColumnDetailRecord;
import com.cfo.reporting.dto.ConceptDetailRecord;
import com.cfo.reporting.dto.ConceptResultDTO;
import com.cfo.reporting.importing.BulkRepositoryImpl;
import com.cfo.reporting.model.*;
import com.cfo.reporting.repository.ConceptDetailsValuesRepository;
import com.cfo.reporting.repository.ConceptRepository;
import com.cfo.reporting.repository.ScreenRepository;
import com.cfo.reporting.service.config.ConsultaConfig;
import com.cfo.reporting.service.config.PantallaConfig;
import com.cfo.reporting.service.config.PantallaConfigRepository;
import com.cfo.reporting.utils.CSVParallelProcessor;
import com.cfo.reporting.utils.ProcessConceptFormulas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.cfo.reporting.service.config.PantallaService;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import com.cfo.reporting.config.ApplicationConstants;
import com.cfo.reporting.utils.SubtractMonth;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Service
public class ConceptParserService {

    private static String PREPROCESS_SCREEN="scr_pr01import";
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

    @Autowired
    private BackgroundSaveService backgroundSaveService;

    @Autowired
    private CSVParallelProcessor cSVParallelProcessor;

    @Autowired
    ScreenRepository screenRepository;

    @Autowired
    DynamicScreensService dynamicScreensService;

    @Autowired
    ProcessConceptFormulas processConceptFormulas;

    public Map<String,Object> allConceptsScreen(String screenId, String glPeriod, Pageable page, int pageNumber, int pageSize) {
        Map<String,Object> allResultsConcepts = new HashMap<>();
        Map<String,Object> pageData = new HashMap<>();


        boolean screenToSave= screenRepository.findByScreenId(screenId).isScreen_save();


        try {
            if (hasSubconcepts(screenId)) {
                List<?> allRetrievedRecords = allConceptsWithSubconcepts(
                        screenId, glPeriod, getTablesData(screenId, glPeriod),
                        screenToSave
                );

                long total = allRetrievedRecords.size();
                int offset = pageNumber * pageSize;
                boolean hasNext = ((pageNumber + 1) * pageSize) < total;
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
                allResultsConcepts = allConceptsWithoutSubconcepts(screenId, glPeriod, page,pageNumber,pageSize);
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
         // retrieving
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

    private Map<String,Object> allConceptsWithoutSubconcepts(String screenId, String glPeriod, Pageable page,int pageNumber, int pageSize) {
        List<Concept> allConcepts = new ArrayList<>();
        List<ConceptResultDTO> allResultsConcepts = new ArrayList<>();
        Map<String,Object> mapConceptResults = new HashMap<>();
        Map<String,Object> pageData = new HashMap<>();
        allConcepts = conceptRepository.allConceptsByScreenId(screenId);
        for(Concept concept : allConcepts)  {
            if (concept.getQuery_concepts() != null && concept.getQuery_concepts().toLowerCase().contains("select")) {
                List<Map<String,Object>> listDetails = new ArrayList<>();
                 //
                 // checks if screen requires load preprocess information
                 //
                //pageData = getPageableData(concept.getQuery_concepts().toLowerCase(),page);
                pageData = getPageableData(concept.getQuery_concepts().toLowerCase(),page,pageNumber,pageSize,glPeriod);
                listDetails = dynamicQueryService.executeDynamicQuery(
                        concept.getQuery_concepts()+" where gl_period ='"+glPeriod+"' LIMIT "+
                                pageSize  +
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
                                               Map<String, Map<String, Object>> tablesData,
                                               boolean screenToSave) throws Exception {
        List<Concept> allParentConcepts = conceptRepository.allParentConcepts(screenId);
        List<ConceptResultDTO> allResultsConcepts = new ArrayList<>();

        for (Concept concept : allParentConcepts) {
            List<ConceptResultDTO> resultsParentSubConcepts = new ArrayList<>();

            List<Concept> allSubconcepts =
                    conceptRepository.allSubConceptsByConceptId(screenId,(int)concept.getConcept_id());
            for (Concept subconcept : allSubconcepts) {
                ConceptResultDTO subConceptResultDTO = conceptWithDetails
                        (screenId, glPeriod, (int) subconcept.getConcept_id(),tablesData);
                subConceptResultDTO = processConceptFormulas.processConceptFormulas(subConceptResultDTO,
                        glPeriod,tablesData);
                // Checks if the column has value in the tbl_cfo_column_details_values
                //
                for (ColumnDetailRecord columnDetail: subConceptResultDTO.getAllColumns() ) {
                    if (columnDetail.getColumnValue () ==0) {
                        getColumnDetailValue(0, (int) (subconcept.getConcept_id()),
                                columnDetail, glPeriod);
                    }
                }
                //
                resultsParentSubConcepts.add(subConceptResultDTO);
            }
            //
             //
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
            //
             // Calculate formulas by concept
             //
            parentConceptDTO = processConceptFormulas.processConceptFormulas(parentConceptDTO,
                    glPeriod,tablesData);
            for (ColumnDetailRecord columnDetail: parentConceptDTO.getAllColumns() ) {
                if (columnDetail.getColumnValue () ==0) {
                    getColumnDetailValue(0, (int) (parentConceptDTO.getConceptId()),
                            columnDetail, glPeriod);
                }
            }
            allResultsConcepts.add(parentConceptDTO);
            allResultsConcepts.addAll(resultsParentSubConcepts);
        }
        //Persist all concepts and Details
        if (screenToSave) {
            saveConceptDetailValues(allResultsConcepts, glPeriod);
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
        return  subConceptsFound == 0;
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
                           listParameters.get(0).getParametros().get(0).getNombre()), glPeriod);
                   parametros.put(String.valueOf(
                                   listParameters.get(1).getParametros().get(0).getNombre()),
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


    private Map<String,Object> getPageableData(String QuerytoExecute, Pageable pageable, int pageNumber, int pageSize,String glPeriod) {
        long totRows = bulkRepository.recordsProcessedByTable(QuerytoExecute.replaceAll("(?i)select\\s+\\*","select count(*) ")+" where gl_period = '"+glPeriod+"'");
        int currOffset =0;
        boolean nextPage;
        int totPages = (int) Math.ceil((double) totRows/ pageSize);
        Map<String,Object> pageData = new HashMap<>();
        if (pageNumber == 0) {
            currOffset=0;
        }
        else {
            currOffset = (pageNumber) * pageSize;
        }

        boolean hasNext = ((pageNumber-1) * pageSize) < totRows;
        boolean hasPrev = ((pageNumber)*pageSize > 0); // 30>0

        pageData.put("offsetPage", currOffset);
        pageData.put("totalPages",totPages);
        pageData.put("totalItems",totRows);
        pageData.put("pageNumber",pageNumber);
        pageData.put("hasPreviousPage",hasPrev);
        pageData.put("hasNextPage",hasNext);
        return pageData;
    }

    private boolean saveConceptDetailValues(List<ConceptResultDTO> listValues,String glPeriod) {
        try {
            List<ConceptDetailValues> conceptsTosave = new ArrayList<>();
            for (ConceptResultDTO conceptResultDTO: listValues){
                for (ColumnDetailRecord columnDetailRecord: conceptResultDTO.getAllColumns()) {
                    ConceptDetailValues conceptDetailValues = new ConceptDetailValues();
                    ConceptDetailValuesKey conceptDetailValueKey = new ConceptDetailValuesKey();
                    conceptDetailValueKey.setConceptId(conceptResultDTO.getConceptId());
                    conceptDetailValueKey.setGlPeriod(glPeriod);
                    conceptDetailValueKey.setConceptDetailId(0);
                    conceptDetailValueKey.setColumnName(columnDetailRecord.getColumnName().replaceAll("[\\s/+%]","_"));
                    conceptDetailValues.setId(conceptDetailValueKey);
                    conceptDetailValues.setColumnValue(columnDetailRecord.getColumnValue());
                    //conceptDetailsValuesRepository.save(conceptDetailValues);
                    conceptsTosave.add(conceptDetailValues);
                }
//                if (conceptResultDTO.getAllColumns().size() == 0 ) {
//                    conceptsTosave.add(conceptDetailValues);
//                }
                for (ConceptDetailRecord conceptDetailRecord: conceptResultDTO.getDetalles()) {
                    int innerDetailColumns= conceptDetailRecord.allColumns().size();
                    for (ColumnDetailRecord columnDetailRecord: conceptDetailRecord.allColumns()) {
                        ConceptDetailValues innerConceptDetailValues = new ConceptDetailValues();
                        ConceptDetailValuesKey innerConceptDetailValueKey = new ConceptDetailValuesKey();
                        innerConceptDetailValueKey.setConceptId(conceptResultDTO.getConceptId());
                        innerConceptDetailValueKey.setGlPeriod(glPeriod);
                        innerConceptDetailValueKey.setConceptDetailId(conceptDetailRecord.detailId());
                        innerConceptDetailValueKey.setColumnName(columnDetailRecord.getColumnName().replaceAll("[\\s/+%]","_"));
                        innerConceptDetailValues.setId(innerConceptDetailValueKey);
                        innerConceptDetailValues.setColumnValue(columnDetailRecord.getColumnValue());
                        conceptsTosave.add(innerConceptDetailValues);
                    }
                }

            }
            //saving data in background Mode
            backgroundSaveService.salvarConAsync(conceptsTosave,ConceptDetailValues.class);

        }
        catch (Exception ex) {

            System.out.println("Exception when saving values :"+ex.getMessage());
        }
        return true;
    }

    private void checkRequireProcessing(String screenId, String glPeriod)  {
        try {
            if (screenId.contains(PREPROCESS_SCREEN)) {
                backgroundSaveService.salvarConAsync(
                        cSVParallelProcessor.processToGenerateCSV(glPeriod),
                        CsvExporting.class);
            }
        } catch(Exception ex) {
            System.out.println("Error when prprocessing Screen ");
        }

    }

    private void getColumnDetailValue(long detail_id,
                                      int concept_id,
                                      ColumnDetailRecord columnDetailRecord,
                                      String glPeriod) {

        ConceptDetailValues conceptDetailValues = conceptDetailsValuesRepository
                .findByScreenConceptDetailAndGlPeriodAndColumn(concept_id,
                        detail_id,
                        glPeriod,
                        columnDetailRecord.getColumnName().replaceAll("[^a-zA-Z0-9]", "_").toUpperCase());
        if (conceptDetailValues!= null) {
            columnDetailRecord.setColumnValue(conceptDetailValues.getColumnValue());
        }
    }

}
