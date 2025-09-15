package com.cfo.reporting.controller;

import com.cfo.reporting.dto.ApiResponse;
import com.cfo.reporting.dto.ConceptDetailValuesDTO;
import com.cfo.reporting.exception.DataScreenProcessingException;
import com.cfo.reporting.model.Concept;
import com.cfo.reporting.model.Header;
import com.cfo.reporting.model.Screen;
import com.cfo.reporting.service.ConceptParserService;
import com.cfo.reporting.service.DynamicScreensService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/dynamic")
@CrossOrigin(origins="*")
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
    public ResponseEntity<Map<String,Object>> getScreen(
            @PathVariable("screenId") String screenId,
            @PathVariable("glPeriod") String glPeriod,
            @RequestParam(value = "pageNumber",required = false) String page,
            @RequestParam(value = "pageSize",required = false) String pageSize,
            @PageableDefault(page=0, size=10) Pageable pageable) {

        int pageNumber = pageable.getPageNumber();
        try {
            pageNumber = (Integer.parseInt(page)!=0) ? Integer.parseInt(page): pageable.getPageNumber();
        }catch (NumberFormatException nfEx){
            pageNumber++;
        }



        Map<String,Object> response = new HashMap<>();
        Map<String,Object> mapResults = conceptParserService.allConceptsScreen(
                screenId,glPeriod,pageable,pageNumber-1);
        Map<String,Object> pageData = (Map<String, Object>) mapResults.get("pageData");
        List<?> listConcepts = (List<?>) mapResults.get("allConcepts");
        response.put("screens",listConcepts);
        response.put("pageNumber",pageNumber);
        response.put("totalItems",pageData.get("totalItems"));
        response.put("totalPages",pageData.get("totalPages"));
        response.put("hasPreviousPage",pageData.get("hasPreviousPage"));
        response.put("hasNextPage",pageData.get("hasNextPage"));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/detailsvalues")
    public ApiResponse<?> createConceptDetailValue (
            @RequestBody ConceptDetailValuesDTO conceptDetailValue
    ) throws DataScreenProcessingException {
        return new ApiResponse<>(dynamicScreensService.saveConceptDetailValue(conceptDetailValue));
    }

    @PutMapping("/detailsvalues")
    public ApiResponse<?> updateConceptDetaiValue (
            @RequestBody ConceptDetailValuesDTO conceptDetailValue
    ) throws DataScreenProcessingException {
        return new ApiResponse<>(dynamicScreensService.updateConceptDetailValue(conceptDetailValue));
    }

    @DeleteMapping("/detailsvalues")
    public ApiResponse<?> deleteConceptDetaiValue (
            @RequestBody ConceptDetailValuesDTO conceptDetailValue
    ) throws DataScreenProcessingException {
        return new ApiResponse<>(dynamicScreensService.deleteConceptDetailValue(conceptDetailValue));
    }

}
