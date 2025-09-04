package com.cfo.reporting.service;

import com.cfo.reporting.dto.ConceptDetailValuesDTO;
import com.cfo.reporting.exception.DataScreenProcessingException;
import com.cfo.reporting.model.Concept;
import com.cfo.reporting.model.Header;
import com.cfo.reporting.model.Screen;

import java.util.List;

public interface DynamicScreensService {

    public List<Screen> getAllScreens();
    public List<Concept> getAllConcepts(String screenId) ;
    public List<Header> getAllHeaders(String screendId) ;

    public ConceptDetailValuesDTO saveConceptDetailValue(ConceptDetailValuesDTO saveceptDetailValuesDTO) throws DataScreenProcessingException;
    public ConceptDetailValuesDTO updateConceptDetailValue(ConceptDetailValuesDTO saveceptDetailValuesDTO) throws DataScreenProcessingException;
    public boolean deleteConceptDetailValue(ConceptDetailValuesDTO saveceptDetailValuesDTO) throws DataScreenProcessingException;

}
