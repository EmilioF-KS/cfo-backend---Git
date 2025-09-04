package com.cfo.reporting.service;

import com.cfo.reporting.dto.ConceptDetailValuesDTO;
import com.cfo.reporting.exception.DataProcessingException;
import com.cfo.reporting.exception.DataScreenProcessingException;
import com.cfo.reporting.model.*;
import com.cfo.reporting.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DynamicScreensServiceImpl implements DynamicScreensService {
    @Autowired
    ConceptRepository conceptRepository;
    @Autowired
    ConceptDetailRepository conceptDetailRepository;
    @Autowired
    ScreenRepository screenRepository;
    @Autowired
    HeadersRepository headersRepository;
    @Autowired
    FormulaRepository formulaRepository;

    @Autowired
    ConceptDetailsValuesRepository conceptDetailsValuesRepository;

    public List<Screen> getAllScreens() {
        return screenRepository.findAll();
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
            ConceptDetailValueKey conceptDetailValueKey = new ConceptDetailValueKey();
            conceptDetailValueKey.setConceptId(saveConceptDetailValuesDTO.getConceptId());
            conceptDetailValueKey.setGlPeriod(saveConceptDetailValuesDTO.getGlPeriod());
            conceptDetailValueKey.setConceptDetailId(saveConceptDetailValuesDTO.getConceptDetailId());
            ConceptDetailValues conceptDetailValues = new ConceptDetailValues();
            conceptDetailValues.setId(conceptDetailValueKey);
            conceptDetailValues.setColumnValue(saveConceptDetailValuesDTO.getColumnValue());
            conceptDetailValues.setColumnName(saveConceptDetailValuesDTO.getColumnName());
            savedConceptDetailValues = conceptDetailsValuesRepository.saveAndFlush(conceptDetailValues);

            if (savedConceptDetailValues != null ) {
                conceptDetailValuesDTO = new ConceptDetailValuesDTO();
                conceptDetailValuesDTO.setColumnValue(savedConceptDetailValues.getColumnValue());
                conceptDetailValuesDTO.setConceptId(savedConceptDetailValues.getId().getConceptId());
                conceptDetailValuesDTO.setConceptDetailId(savedConceptDetailValues.getId().getConceptDetailId());
                conceptDetailValuesDTO.setColumnName(savedConceptDetailValues.getColumnName());
                return conceptDetailValuesDTO;
            }
            if (conceptDetailValuesDTO != null ) {
                conceptDetailValueKey = new ConceptDetailValueKey();
                conceptDetailValueKey.setConceptId(conceptDetailValuesDTO.getConceptId());
                conceptDetailValueKey.setConceptDetailId(conceptDetailValuesDTO.getConceptDetailId());
                conceptDetailValues = new ConceptDetailValues();
                conceptDetailValues.setId(conceptDetailValueKey);
                conceptDetailValues.setColumnValue(conceptDetailValuesDTO.getColumnValue());
                conceptDetailValues.setColumnName(conceptDetailValuesDTO.getColumnName());
                conceptDetailValuesDTO.setColumnValue(savedConceptDetailValues.getColumnValue());
                conceptDetailsValuesRepository.delete(conceptDetailValues);
                return conceptDetailValuesDTO;
            }
        }  catch(Exception ex) {
            throw new DataScreenProcessingException("Error when saving ConceptDetailValue ",ex.getCause());
        }
       return new ConceptDetailValuesDTO();
    }

    @Override
    public ConceptDetailValuesDTO updateConceptDetailValue(ConceptDetailValuesDTO updateConceptDetailValuesDTO) throws DataScreenProcessingException {
        ConceptDetailValues savedConceptDetailValues = null;
        ConceptDetailValuesDTO conceptDetailValuesDTO = null;
        try {
            if (updateConceptDetailValuesDTO.getConceptDetailId() == 0) {
                conceptDetailValuesDTO = conceptDetailsValuesRepository.findByScreenConceptAndGlPeriod(
                        updateConceptDetailValuesDTO.getScreenId(),
                        updateConceptDetailValuesDTO.getConceptId(),
                        updateConceptDetailValuesDTO.getGlPeriod());
            } else {
                conceptDetailValuesDTO = conceptDetailsValuesRepository.findByScreenConceptDetailAndGlPeriod(
                        updateConceptDetailValuesDTO.getScreenId(),
                        updateConceptDetailValuesDTO.getConceptId(),
                        updateConceptDetailValuesDTO.getConceptDetailId(),
                        updateConceptDetailValuesDTO.getGlPeriod());
            }
            if (conceptDetailValuesDTO != null ) {
                ConceptDetailValueKey conceptDetailValueKey = new ConceptDetailValueKey();
                conceptDetailValueKey.setConceptId(conceptDetailValuesDTO.getConceptId());
                conceptDetailValueKey.setConceptDetailId(conceptDetailValuesDTO.getConceptDetailId());
                ConceptDetailValues conceptDetailValues = new ConceptDetailValues();
                conceptDetailValues.setId(conceptDetailValueKey);
                conceptDetailValues.setColumnValue(conceptDetailValuesDTO.getColumnValue());
                conceptDetailValues.setColumnName(conceptDetailValuesDTO.getColumnName());
                conceptDetailValuesDTO.setColumnValue(savedConceptDetailValues.getColumnValue());
                conceptDetailsValuesRepository.save(conceptDetailValues);
                return conceptDetailValuesDTO;
            }
        }  catch(Exception ex) {
            throw new DataScreenProcessingException("Error when updating ConceptDetailValue ",ex.getCause());
        }
        return new ConceptDetailValuesDTO();

    }

    @Override
    public boolean deleteConceptDetailValue(ConceptDetailValuesDTO deleteConcepttDetailValuesDTO) throws DataScreenProcessingException {
        ConceptDetailValues savedConceptDetailValues = null;
        ConceptDetailValuesDTO conceptDetailValuesDTO = null;
        try {
            if (deleteConcepttDetailValuesDTO.getConceptDetailId() == 0) {
                conceptDetailValuesDTO = conceptDetailsValuesRepository.findByScreenConceptAndGlPeriod(
                        deleteConcepttDetailValuesDTO.getScreenId(),
                        deleteConcepttDetailValuesDTO.getConceptId(),
                        deleteConcepttDetailValuesDTO.getGlPeriod());
            } else {
                conceptDetailValuesDTO = conceptDetailsValuesRepository.findByScreenConceptDetailAndGlPeriod(
                        deleteConcepttDetailValuesDTO.getScreenId(),
                        deleteConcepttDetailValuesDTO.getConceptId(),
                        deleteConcepttDetailValuesDTO.getConceptDetailId(),
                        deleteConcepttDetailValuesDTO.getGlPeriod());
            }
            if (conceptDetailValuesDTO != null ) {
                ConceptDetailValueKey conceptDetailValueKey = new ConceptDetailValueKey();
                conceptDetailValueKey.setConceptId(conceptDetailValuesDTO.getConceptId());
                conceptDetailValueKey.setConceptDetailId(conceptDetailValuesDTO.getConceptDetailId());
                ConceptDetailValues conceptDetailValues = new ConceptDetailValues();
                conceptDetailValues.setId(conceptDetailValueKey);
                conceptDetailValues.setColumnValue(conceptDetailValuesDTO.getColumnValue());
                conceptDetailValues.setColumnName(conceptDetailValuesDTO.getColumnName());
                conceptDetailValuesDTO.setColumnValue(savedConceptDetailValues.getColumnValue());
                conceptDetailsValuesRepository.save(conceptDetailValues);
                return true;
            }
        }
        catch(Exception ex ){
            throw new DataScreenProcessingException("Error when updating ConceptDetailValue ",ex.getCause());
        }
        return false;
    }
}
