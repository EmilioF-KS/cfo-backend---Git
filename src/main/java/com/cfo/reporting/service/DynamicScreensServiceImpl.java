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
            ConceptDetailValues conceptDetailValues = new ConceptDetailValues();
            ConceptDetailValuesKey conceptDetailValuesKey = new ConceptDetailValuesKey();
            conceptDetailValuesKey.setConceptId(saveConceptDetailValuesDTO.getConceptId());
            conceptDetailValuesKey.setGlPeriod(saveConceptDetailValuesDTO.getGlPeriod());
            conceptDetailValuesKey.setConceptDetailId(saveConceptDetailValuesDTO.getConceptDetailId());
            conceptDetailValues.setId(conceptDetailValuesKey);
            conceptDetailValues.setColumnValue(saveConceptDetailValuesDTO.getColumnValue());
            conceptDetailValuesKey.setColumnName(saveConceptDetailValuesDTO.getColumnName());
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
                conceptDetailValuesKey.setConceptDetailId(conceptDetailValuesDTO.getConceptDetailId());
                conceptDetailValuesKey.setGlPeriod(saveConceptDetailValuesDTO.getGlPeriod());
                conceptDetailValuesKey.setColumnName(saveConceptDetailValuesDTO.getColumnName());
                conceptDetailValues.setId(conceptDetailValuesKey);
                conceptDetailValuesDTO.setColumnValue(savedConceptDetailValues.getColumnValue());
                conceptDetailsValuesRepository.save(conceptDetailValues);
                return conceptDetailValuesDTO;
            }
        }  catch(Exception ex) {
            throw new DataScreenProcessingException("Error when saving ConceptDetailValue ",ex.getCause());
        }
       return new ConceptDetailValuesDTO();
    }

    @Override
    public ConceptDetailValuesDTO updateConceptDetailValue(ConceptDetailValuesDTO updateConceptDetailValuesDTO) throws DataScreenProcessingException {
        ConceptDetailValues updatedConceptDetailValues = null;
        ConceptDetailValuesDTO conceptDetailValuesDTO = null;
        try {
            if (updateConceptDetailValuesDTO.getConceptDetailId() == 0) {
                updatedConceptDetailValues = conceptDetailsValuesRepository.findByScreenConceptAndGlPeriod(
                        updateConceptDetailValuesDTO.getConceptId(),
                        updateConceptDetailValuesDTO.getGlPeriod());
            } else {
                updatedConceptDetailValues = conceptDetailsValuesRepository.findByScreenConceptDetailAndGlPeriod(
                        updateConceptDetailValuesDTO.getConceptId(),
                        updateConceptDetailValuesDTO.getConceptDetailId(),
                        updateConceptDetailValuesDTO.getGlPeriod());
            }
            if (updatedConceptDetailValues != null ) {
                updatedConceptDetailValues.setColumnValue(updateConceptDetailValuesDTO.getColumnValue());
                conceptDetailsValuesRepository.save(updatedConceptDetailValues);
                return updateConceptDetailValuesDTO;
            }
        }  catch(Exception ex) {
            throw new DataScreenProcessingException("Error when updating ConceptDetailValue ",ex.getCause());
        }
        return new ConceptDetailValuesDTO();

    }

    @Override
    public boolean deleteConceptDetailValue(ConceptDetailValuesDTO deleteConcepttDetailValuesDTO) throws DataScreenProcessingException {
        ConceptDetailValues deletedConceptDetailValues = null;
        ConceptDetailValuesDTO conceptDetailValuesDTO = null;
        try {
            if (deleteConcepttDetailValuesDTO.getConceptDetailId() == 0) {
                deletedConceptDetailValues = conceptDetailsValuesRepository.findByScreenConceptAndGlPeriod(
                        deleteConcepttDetailValuesDTO.getConceptId(),
                        deleteConcepttDetailValuesDTO.getGlPeriod());
            } else {
                deletedConceptDetailValues = conceptDetailsValuesRepository.findByScreenConceptDetailAndGlPeriod(
                        deleteConcepttDetailValuesDTO.getConceptId(),
                        deleteConcepttDetailValuesDTO.getConceptDetailId(),
                        deleteConcepttDetailValuesDTO.getGlPeriod());
            }
            if (deletedConceptDetailValues != null ) {
                conceptDetailsValuesRepository.delete(deletedConceptDetailValues);
                return true;
            }
        }
        catch(Exception ex ){
            throw new DataScreenProcessingException("Error when updating ConceptDetailValue ",ex.getCause());
        }
        return false;
    }
}
