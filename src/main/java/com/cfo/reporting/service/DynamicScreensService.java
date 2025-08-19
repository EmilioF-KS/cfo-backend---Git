package com.cfo.reporting.service;

import com.cfo.reporting.dto.ScreenDTO;
import com.cfo.reporting.model.Concept;
import com.cfo.reporting.model.Header;
import com.cfo.reporting.model.Screen;
import com.cfo.reporting.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DynamicScreensService {
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

    public List<Screen> getAllScreens() {
        return screenRepository.findAll();
    }

    public List<Concept> getAllConcepts(String screenId) {

        return conceptRepository.allConceptsByScreenId(screenId);
    }


    public List<Header> getAllHeaders(String screendId) {
        return headersRepository.allHeadersByScreenId(screendId);
    }
}
