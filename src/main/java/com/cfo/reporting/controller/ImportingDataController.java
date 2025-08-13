package com.cfo.reporting.controller;

import com.cfo.reporting.dto.ScreenDTO;
import com.cfo.reporting.model.Screen;
import com.cfo.reporting.model.UpdateTables;
import com.cfo.reporting.service.DynamicScreensService;
import com.cfo.reporting.service.UpdatedTablesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://172.18.128.1:8080") 
@RequestMapping("/import/api")
public class ImportingDataController {

    @Autowired
    UpdatedTablesService  updatedTablesService;
    @Autowired
    DynamicScreensService  dynamicScreensService;
    @GetMapping("/tablestoimport")
    public List<UpdateTables> getTablesToImport() {
      return updatedTablesService.allUpdatedTables();

    }

    @GetMapping("/screens")
    public List<Screen> allScreens() {
        return dynamicScreensService.getAllScreens();
    }
    
    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome everyone";
    }
}
