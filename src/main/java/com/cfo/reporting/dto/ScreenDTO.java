package com.cfo.reporting.dto;

import com.cfo.reporting.model.Concept;
import com.cfo.reporting.model.Header;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
@Data
@AllArgsConstructor
public class ScreenDTO {
    private Long id;
    private String screenName;
    private String screenDescripcion;
}