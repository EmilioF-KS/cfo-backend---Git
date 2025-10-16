package com.cfo.reporting.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScreenReportDTO {
    private String reptype_id;
    private String reptype_description;
    private int reptype_order;
}