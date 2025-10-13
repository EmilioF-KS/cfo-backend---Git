package com.cfo.reporting.controller;

import com.cfo.reporting.dto.ApiResponse;
import com.cfo.reporting.dto.ConceptDetailValuesDTO;
import com.cfo.reporting.dto.ScreenRepCategoryDTO;
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



    @GetMapping("/screens/{reptype}")
    public ApiResponse<ScreenRepCategoryDTO> allScreens(@PathVariable("reptype") String reptype) {

        return new ApiResponse<>(dynamicScreensService.getAllScreens(reptype));
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
        int pageablePageSize = pageable.getPageSize();

        try {
            pageNumber = (Integer.parseInt(page)!=0) ? Integer.parseInt(page): pageable.getPageNumber();
        }catch (NumberFormatException nfEx){
            pageNumber++;
        }



        try {
            pageablePageSize = (Integer.parseInt(pageSize)!=0) ? Integer.parseInt(pageSize): pageable.getPageNumber();
        }catch (NumberFormatException nfEx){
        }

        System.out.println("Set page: "+page+" with page size: "+pageablePageSize);

        Map<String,Object> response = new HashMap<>();
        Map<String,Object> mapResults = conceptParserService.allConceptsScreen(
                screenId,glPeriod,pageable,pageNumber-1,pageablePageSize);
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
            @RequestBody ConceptDetailValuesDTO[] conceptDetailValues
    ) throws DataScreenProcessingException {

        try {
            for (ConceptDetailValuesDTO conceptDetailValuesDTO : conceptDetailValues) {
                dynamicScreensService.saveConceptDetailValue(conceptDetailValuesDTO);
            }
            return new ApiResponse<>("Values Updated ");
        }
        catch(Exception ex) {
            return new ApiResponse<>("Error when Saving Column Values :"+ex.getMessage());
        }
    }



}
