package com.cfo.reporting.service;

import com.cfo.reporting.dto.ConceptDetailValuesDTO;
import com.cfo.reporting.dto.ScreenRepCategoryDTO;
import com.cfo.reporting.exception.DataScreenProcessingException;
import com.cfo.reporting.model.Concept;
import com.cfo.reporting.model.Header;
import com.cfo.reporting.model.Screen;

import java.util.List;

public interface DynamicScreensService {

    public ScreenRepCategoryDTO getAllScreens(String reptype);
    public List<Concept> getAllConcepts(String screenId) ;
    public List<Header> getAllHeaders(String screendId) ;

    public ConceptDetailValuesDTO saveConceptDetailValue(ConceptDetailValuesDTO saveceptDetailValuesDTO) throws DataScreenProcessingException;

}
