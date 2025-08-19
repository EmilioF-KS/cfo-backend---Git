package com.cfo.reporting.importing;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface ProcessExcellStrategy {
    int processExcel(File file, String glPeriod) throws Exception;
}
