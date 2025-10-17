package com.cfo.reporting.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScreenReportDTO {

    private String reptype_id;
    private String reptype_description;
    @JsonIgnore
    private int reptype_order;
    private String screenId;
}