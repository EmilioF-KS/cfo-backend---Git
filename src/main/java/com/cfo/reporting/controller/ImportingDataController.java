package com.cfo.reporting.controller;

import com.cfo.reporting.dto.ApiResponse;
import com.cfo.reporting.dto.FileProcessingDTO;
import com.cfo.reporting.exception.DataProcessingException;
import com.cfo.reporting.model.UpdateTables;
import com.cfo.reporting.service.CsvBlobService;
import com.cfo.reporting.service.FileStorageService;
import com.cfo.reporting.service.GlPeriodService;
import com.cfo.reporting.service.UpdatedTablesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("api/import")
@CrossOrigin(origins="*")
public class ImportingDataController {

    @Autowired
    UpdatedTablesService  updatedTablesService;

    @Autowired
    FileStorageService fileStorageService;

    @Autowired
    GlPeriodService  glPeriodService;

    @Autowired
    private CsvBlobService csvBlobService;

    @GetMapping("/tablestoimport")
    public List<UpdateTables> getTablesToImport() {
      return updatedTablesService.allUpdatedTables();

    }

    @GetMapping("/glperiods")
    public ApiResponse<?> getAllPeriods() {
        return new ApiResponse<>(glPeriodService.allPeriods());

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


    @GetMapping("/generateCsv/{glPeriod}/{screenId}")
    public ResponseEntity<byte[]> downloadCsv( @PathVariable("glPeriod") String glperiod,
                                               @PathVariable("screenId") String screenId) {
        try {
            Blob csvBlob = csvBlobService.generateCsvCFO(glperiod,screenId);

            byte[] csvBytes = csvBlob.getBytes(1, (int) csvBlob.length());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=datos.csv")
                    .header(HttpHeaders.CONTENT_TYPE, "text/csv; charset=UTF-8")
                    .body(csvBytes);

        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
