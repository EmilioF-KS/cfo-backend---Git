package com.cfo.reporting.controller;

import com.cfo.reporting.dto.ApiResponse;
import com.cfo.reporting.dto.ConceptResultDTO;
import com.cfo.reporting.model.Concept;
import com.cfo.reporting.model.Header;
import com.cfo.reporting.model.Screen;
import com.cfo.reporting.service.ConceptParserService;
import com.cfo.reporting.service.DynamicScreensService;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/dynamic")
public class DynamicScreenController {
    @Autowired
    DynamicScreensService dynamicScreensService;
    @Autowired
    ConceptParserService conceptParserService;

    @GetMapping("/screens")
    public ApiResponse<List<Screen>> allScreens() {
        return new ApiResponse<>(dynamicScreensService.getAllScreens());
    }

    @GetMapping("/headers/{screenId}")
    public ApiResponse<List<Header>> screenHeaders(@PathVariable("screenId") String screenId) {
        return new ApiResponse<>(dynamicScreensService.getAllHeaders(screenId));
    }

    @GetMapping("/concepts/{screenId}")
    public ApiResponse<List<Concept>> screenConcepts(@PathVariable("screenId") String screenId) {
        return new ApiResponse<>(dynamicScreensService.getAllConcepts(screenId));
    }
    @GetMapping("/screens/{screenId}/{glPeriod}")
    public ApiResponse<List<?>> getScreen(
            @PathVariable("screenId") String screenId,
            @PathVariable("glPeriod") String glPeriod) {
        return new ApiResponse<>(conceptParserService.allConceptsScreen(screenId,glPeriod));
    }
}
