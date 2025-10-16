package com.cfo.reporting.service;

import com.cfo.reporting.dto.ConceptDetailValuesDTO;
import com.cfo.reporting.dto.ScreenMenuItemDTO;
import com.cfo.reporting.dto.ScreenRepCategoryDTO;
import com.cfo.reporting.dto.ScreenReportDTO;
import com.cfo.reporting.exception.DataProcessingException;
import com.cfo.reporting.exception.DataScreenProcessingException;
import com.cfo.reporting.model.*;
import com.cfo.reporting.repository.*;
import jakarta.transaction.Transactional;
import org.drools.core.rule.Collect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DynamicScreensServiceImpl implements DynamicScreensService {
    @Autowired
    ConceptRepository conceptRepository;
    @Autowired
    ConceptDetailRepository conceptDetailRepository;
    @Autowired
    ScreenRepository screenRepository;
    @Autowired
    ScreenRepCategoryRepository screenRepCategoryRepository;
    @Autowired
    HeadersRepository headersRepository;
    @Autowired
    FormulaRepository formulaRepository;

    @Autowired
    ConceptDetailsValuesRepository conceptDetailsValuesRepository;
    @Autowired
    ScreenReportsRepository screenReportsRepository;

    public ScreenRepCategoryDTO getAllScreens(String reptype) {

        ScreenRepCategoryDTO screensReportCategoryDTO = new ScreenRepCategoryDTO();
        screensReportCategoryDTO.setRepId(reptype);
        ScreensReportCategory screensReportCategory = new ScreensReportCategory();
        Map<String,List<ScreensReportCategory>> screenCategory =
                screenRepCategoryRepository.allScreensByRepId(reptype)
                .stream().collect(
                 Collectors.groupingBy(
                       pantalla -> pantalla.getId().getCategoryId()));
        Map<String,List<ScreenMenuItemDTO>> resultMap = new HashMap<>();
        screenCategory.forEach((category,listScreens) -> {
            List<ScreenMenuItemDTO> screens = listScreens.stream()
                    .map(p-> new ScreenMenuItemDTO(p.getId().getScreenId(),
                            p.getScreen().getScreenName(),p.getScreen().getDescription()))
                    .collect(Collectors.toList());
            resultMap.put(category,screens);
        });

        screensReportCategoryDTO.setCategoryScreens(resultMap);

        return screensReportCategoryDTO;
    }

    public List<ScreenReportDTO> getAllMainReportsScreen() {
        List<ScreenReportDTO> screens = screenReportsRepository.allReportsScreen().stream()
                .map(p-> new ScreenReportDTO(p.getReptypeId(),
                        p.getReptypeDesc(),p.getReptypeOrder()))
                .collect(Collectors.toList());
        return screens;
    }

    public List<Concept> getAllConcepts(String screenId) {

        return conceptRepository.allConceptsByScreenId(screenId);
    }


    public List<Header> getAllHeaders(String screendId) {
        return headersRepository.allHeadersByScreenId(screendId);
    }

    @Transactional
    public ConceptDetailValuesDTO saveConceptDetailValue(ConceptDetailValuesDTO saveConceptDetailValuesDTO) throws DataScreenProcessingException {
        ConceptDetailValues savedConceptDetailValues = null;
        ConceptDetailValuesDTO conceptDetailValuesDTO = null;
        try {
            ConceptDetailValues conceptDetailValues = new ConceptDetailValues();
            ConceptDetailValuesKey conceptDetailValuesKey = new ConceptDetailValuesKey();
            conceptDetailValuesKey.setConceptId(saveConceptDetailValuesDTO.getConceptId());
            conceptDetailValuesKey.setGlPeriod(saveConceptDetailValuesDTO.getGlPeriod());
            conceptDetailValuesKey.setConceptDetailId(saveConceptDetailValuesDTO.getDetailId());
            conceptDetailValues.setId(conceptDetailValuesKey);
            conceptDetailValues.setColumnValue(saveConceptDetailValuesDTO.getColumnValue());
            conceptDetailValuesKey.setColumnName(saveConceptDetailValuesDTO.getColumnName().replaceAll("[\\s/+%]","_"));
            savedConceptDetailValues = conceptDetailsValuesRepository.saveAndFlush(conceptDetailValues);

            if (savedConceptDetailValues != null ) {
                conceptDetailValuesDTO = new ConceptDetailValuesDTO();
                conceptDetailValuesDTO.setColumnValue(savedConceptDetailValues.getColumnValue());
                conceptDetailValuesDTO.setColumnName(savedConceptDetailValues.getId().getColumnName());
                return conceptDetailValuesDTO;
            }
            if (conceptDetailValuesDTO != null ) {
                 conceptDetailValuesKey = new ConceptDetailValuesKey();
                conceptDetailValues = new ConceptDetailValues();
                conceptDetailValuesKey.setConceptId(conceptDetailValuesDTO.getConceptId());
                conceptDetailValuesKey.setConceptDetailId(conceptDetailValuesDTO.getDetailId());
                conceptDetailValuesKey.setGlPeriod(saveConceptDetailValuesDTO.getGlPeriod());
                conceptDetailValuesKey.setColumnName(saveConceptDetailValuesDTO.getColumnName());
                conceptDetailValues.setId(conceptDetailValuesKey);
                conceptDetailValuesDTO.setColumnValue(savedConceptDetailValues.getColumnValue());
                ConceptDetailValues conceptDetailValues1 = conceptDetailsValuesRepository.save(conceptDetailValues);
                ConceptDetailValuesDTO conceptReturnDetailValuesDTO = new ConceptDetailValuesDTO();
                conceptReturnDetailValuesDTO.setGlPeriod(saveConceptDetailValuesDTO.getGlPeriod());
                conceptReturnDetailValuesDTO.setConceptId(conceptDetailValues1.getId().getConceptId());
                conceptReturnDetailValuesDTO.setDetailId(conceptDetailValues1.getId().getConceptDetailId());
                conceptReturnDetailValuesDTO.setColumnName(conceptDetailValues1.getId().getColumnName());
                conceptReturnDetailValuesDTO.setColumnValue(conceptDetailValues1.getColumnValue());
                return conceptReturnDetailValuesDTO;
            }
        }  catch(Exception ex) {
            throw new DataScreenProcessingException("Error when saving ConceptDetailValue ",ex.getCause());
        }
       return new ConceptDetailValuesDTO();
    }


}
