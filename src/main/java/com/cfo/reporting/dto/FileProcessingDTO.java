package com.cfo.reporting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileProcessingDTO {
    private String fileName;
    private int recordsProcessed;
    private boolean isProcessed;
    private String message;
}
