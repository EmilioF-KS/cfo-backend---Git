package com.cfo.reporting.controller;

import com.cfo.reporting.dto.ApiResponse;
import com.cfo.reporting.dto.FileProcessingDTO;
import com.cfo.reporting.exception.DataProcessingException;
import com.cfo.reporting.model.Concept;
import com.cfo.reporting.model.Header;
import com.cfo.reporting.model.Screen;
import com.cfo.reporting.model.UpdateTables;
import com.cfo.reporting.service.DynamicScreensService;
import com.cfo.reporting.service.FileStorageService;
import com.cfo.reporting.service.UpdatedTablesService;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/import")
public class ImportingDataController {

    @Autowired
    UpdatedTablesService  updatedTablesService;

    @Autowired
    FileStorageService fileStorageService;

    @GetMapping("/tablestoimport")
    public List<UpdateTables> getTablesToImport() {
      return updatedTablesService.allUpdatedTables();

    }

    @PostMapping("/files")
    public ApiResponse<?> processFiles(
            @RequestParam("glperiod") String glperiod,
            @RequestParam("files[]") MultipartFile[] file) {
        // Validación básica
        List<FileProcessingDTO> filesProcessed = null;
        if (file == null || file.length == 0) {
            return new ApiResponse<>("Send at least one file");
        }
        try {
             filesProcessed = fileStorageService.storeFile(file,glperiod);
        } catch (DataProcessingException e) {
            return new ApiResponse<>("Error: "+e.getMessage());
        }

        return new ApiResponse<>(filesProcessed);
    }
}
