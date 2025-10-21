package com.cfo.reporting.service;

import com.cfo.reporting.dto.ConceptDetailValuesDTO;
import com.cfo.reporting.dto.ScreenMenuItemDTO;
import com.cfo.reporting.dto.ScreenRepCategoryDTO;
import com.cfo.reporting.dto.ScreenReportDTO;
import com.cfo.reporting.exception.DataScreenProcessingException;
import com.cfo.reporting.model.*;

import java.util.List;

public interface DynamicScreensService {

    public ScreenRepCategoryDTO getAllScreens(String reptype);
    public List<Concept> getAllConcepts(String screenId) ;
    public List<Header> getAllHeaders(String screendId) ;
    public List<ScreenReportDTO> getAllMainReportsScreen();
//    public ScreensReportCategory reportsScreenById (String screenId,String reptypeId);

    public ConceptDetailValuesDTO saveConceptDetailValue(ConceptDetailValuesDTO saveceptDetailValuesDTO) throws DataScreenProcessingException;

}
