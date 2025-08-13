package com.cfo.reporting.service;

import com.cfo.reporting.model.UpdateTables;
import com.cfo.reporting.repository.UpdatedTablesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UpdatedTablesService {

    @Autowired
    UpdatedTablesRepository updatedTablesRepository;

    public List<UpdateTables> allUpdatedTables() {
        return updatedTablesRepository.findAll();
    }
}
