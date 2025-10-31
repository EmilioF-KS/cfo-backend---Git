package com.cfo.reporting.controller;

import com.cfo.reporting.cache.QueryParams;
import com.cfo.reporting.dto.*;
import com.cfo.reporting.exception.DataScreenProcessingException;
import com.cfo.reporting.model.Concept;
import com.cfo.reporting.model.Header;
import com.cfo.reporting.model.Screen;
import com.cfo.reporting.repository.ScreenRepository;
import com.cfo.reporting.service.ConceptParserService;
import com.cfo.reporting.service.DynamicScreensService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
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
    @Autowired
    ScreenRepository screenRepository;

    @GetMapping("/screens/menu")
    public ApiResponse<List<ScreenReportDTO>> allRepMainScreens() {

        return new ApiResponse<>(dynamicScreensService.getAllMainReportsScreen());
    }
    @GetMapping("/screens/menu/{reptype}")
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
    @GetMapping("/screens/{reptype}/{screenId}/{glPeriod}")
    public ResponseEntity<Map<String,Object>> getScreen(
            @PathVariable("reptype") String reptype,
            @PathVariable("screenId") String screenId,
            @PathVariable("glPeriod") String glPeriod,
            @RequestParam(value = "pageNumber",required = false) String page,
            @RequestParam(value = "pageSize",required = false) String pageSize,
            @PageableDefault(page=0, size=10) Pageable pageable) {

        int pageNumber = pageable.getPageNumber();
        int pageablePageSize = pageable.getPageSize();

        try {
            pageNumber = (Integer.parseInt(page)!=0) ? Integer.parseInt(page): pageable.getPageNumber();
            pageablePageSize = (Integer.parseInt(pageSize)!=0) ? Integer.parseInt(pageSize): pageable.getPageNumber();
        }catch (NumberFormatException nfEx){
            pageNumber++;
        }

        try {
            Screen screen = screenRepository.findByScreenId(screenId);
            String screenName = screen.getScreenName();
            QueryParams queryParams = QueryParams.builder()
                    .reportType(reptype)
                    .glPeriod(glPeriod)
                    .pageNumber(pageNumber - 1)
                    .pageSize(pageablePageSize)
                    .page(pageable)
                    .screen(screen)
                    .build();

            Map<String, Object> response = new HashMap<>();
            Map<String, Object> mapResults = conceptParserService.allConceptsScreen(queryParams);
            Map<String, Object> pageData = (Map<String, Object>) mapResults.get("pageData");
            List<?> listConcepts = (List<?>) mapResults.get("allConcepts");
            response.put("reptypeId", reptype);
            response.put("screenId", screenName);
            response.put("screens", listConcepts);
            response.put("pageNumber", pageNumber);
            response.put("totalItems", pageData.get("totalItems"));
            response.put("totalPages", pageData.get("totalPages"));
            response.put("hasPreviousPage", pageData.get("hasPreviousPage"));
            response.put("hasNextPage", pageData.get("hasNextPage"));
            return ResponseEntity.ok(response);
        }
        catch(Exception ex) {
            Map<String, Object> errors = new HashMap<>();
            errors.put("screenId",screenId);
            errors.put("error",ex.getMessage());
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(errors);
        }
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
