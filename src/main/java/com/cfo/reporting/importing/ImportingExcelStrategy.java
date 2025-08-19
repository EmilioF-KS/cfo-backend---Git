package com.cfo.reporting.importing;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Component
public class ImportingExcelStrategy {

    private ProcessExcellStrategy processExcellStrategy;
    private File file;
    private String glPeriod;

    public ImportingExcelStrategy() {

    }
    public ImportingExcelStrategy(ProcessExcellStrategy processExcellStrategy, File file, String glPeriod) {
        this.processExcellStrategy = processExcellStrategy;
        this.file = file;
        this.glPeriod= glPeriod;
    }

    public void executeProcessing() throws Exception {
        if (processExcellStrategy != null ) {
            processExcellStrategy.processExcel(file,glPeriod);
        } else {
            System.out.println("No strategy selected");
        }
    }
 }
