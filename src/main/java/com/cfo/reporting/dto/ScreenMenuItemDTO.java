package com.cfo.reporting.dto;

import com.cfo.reporting.model.Concept;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScreenMenuItemDTO {
    private String screen_id;
    private String screen_name;
    private String screen_description;
}