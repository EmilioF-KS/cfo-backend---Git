package com.cfo.reporting.service;

import com.cfo.reporting.dto.GlPeriodDTO;
import com.cfo.reporting.model.GlPeriod;
import com.cfo.reporting.repository.GlPeriodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GlPeriodService {
    @Autowired
    GlPeriodRepository glPeriodRepository;

    public List<GlPeriod> allPeriods() {

        return glPeriodRepository.findAll();
    }
}
