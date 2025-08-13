package com.cfo.reporting.service;

import com.cfo.reporting.dto.ScreenDTO;
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
        return screenRepository.findALlWithoutAssociations();
    }

}
