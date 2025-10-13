package com.cfo.reporting.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScreenRepCategoryDTO {
    private String repId;
    private Map<String, List<String>> categoryScreens;
}
