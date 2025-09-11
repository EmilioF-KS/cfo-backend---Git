package com.cfo.reporting.service;

import com.cfo.reporting.dto.FileProcessingDTO;
import com.cfo.reporting.exception.DataProcessingException;
import com.cfo.reporting.importing.BulkRepositoryImpl;
import com.cfo.reporting.importing.ImportingExcelStrategy;
import com.cfo.reporting.importing.ProcessingExcel;
import com.cfo.reporting.importing.ProcessingExcelGLDAYS;
import com.cfo.reporting.repository.UpdatedTablesRepository;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileStorageService {

    @Autowired
    BulkRepositoryImpl bulkRepository;
    @Autowired
    UpdatedTablesRepository updatedTablesRepository;

    final static String GLDAYS_FILE = "gldays";
    private ProcessingExcel processingExcelNonGLDAYS;
    private ImportingExcelStrategy importingExcelStrategy;

    public List<FileProcessingDTO> storeFile(MultipartFile[] files, String periodo) throws DataProcessingException {
        List<FileProcessingDTO> filesProcessed = new ArrayList<>();
        try {
            for (MultipartFile file : files) {
                if (file.getOriginalFilename().toLowerCase().contains(GLDAYS_FILE)) {
                    ProcessingExcelGLDAYS processingExcelGLDAYS =
                            new ProcessingExcelGLDAYS(bulkRepository,updatedTablesRepository);
                    FileProcessingDTO fileProcessingDTO = FileProcessingDTO.builder()
                            .fileName(file.getName())
                            .recordsProcessed(processingExcelGLDAYS.importGLDAYSExcelFile
                                    (convertMultiPartToFile(file), periodo))
                            .build();
                    filesProcessed.add(fileProcessingDTO);
                }
                else {
                    ProcessingExcel processingExcel =
                            new ProcessingExcel(bulkRepository,updatedTablesRepository);
                    FileProcessingDTO fileProcessingDTO = FileProcessingDTO.builder()
                            .fileName(file.getOriginalFilename())
                            .recordsProcessed(processingExcel.processExcel
                                    (convertMultiPartToFile(file), periodo))
                            .build();
                    if (fileProcessingDTO.getRecordsProcessed() < 0 ) {
                        fileProcessingDTO.setProcessed(false);
                        fileProcessingDTO.setRecordsProcessed(0);
                        fileProcessingDTO.setMessage("Table already processed");
                    }
                    filesProcessed.add(fileProcessingDTO);

                }

            }

            return filesProcessed;

        } catch (Exception e) {
            throw new DataProcessingException("Error while processing files -> "+e.getMessage()+" Cause "+e.getCause(),e);
        }
    }

    private File convertMultiPartToFile(MultipartFile multFile) {
        File file = new File(System.getProperty("java.io.tmpdir")+"/"+multFile.getOriginalFilename());
        try {
            multFile.transferTo(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return file;
    }



}
