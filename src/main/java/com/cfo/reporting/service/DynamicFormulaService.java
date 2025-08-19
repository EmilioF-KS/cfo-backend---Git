package com.cfo.reporting.service;

import com.cfo.reporting.model.Concept;
import com.cfo.reporting.model.Screen;
import com.cfo.reporting.repository.ScreenRepository;
//import com.cfo.reporting.utils.FormulaEvaluator;
import com.cfo.reporting.utils.FormulaResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DynamicFormulaService {

//    @Autowired
//    private FormulaEvaluator formulaEvaluator;

    @Autowired
    private ScreenRepository screenRepository;

    public List<Screen> evaluateScreenFormulas(String screenId) {
        //Screen screen = screenRepository.findByScreenId(screenId);

       // List<FormulaResult> formulaResults = formulaEvaluator.evaluateFormulas(screenId);

       // return generateScreenResults(screen, formulaResults);
        return null;
    }

    private List<Screen> generateScreenResults(Screen screen, List<FormulaResult> formulaResults) {
//        List<ScreenResult> results = new ArrayList<>();
//
//        for (Concept concept : screen.getConcepts()) {
//            ScreenResult result = new ScreenResult();
//            result.setConceptId(concept.getId());
//            result.setConceptName(concept.getName());
//
//            Map<String, Object> columnValues = new HashMap<>();
//            for (FormulaFormula formulaResult : formulaResults) {
//                columnValues.put(formulaResult.getColumnName(),
//                        evaluateForConcept(formulaResult, concept));
//            }
//
//            result.setColumnValues(columnValues);
//            results.add(result);
//        }

        return null;
    }

    private Object evaluateForConcept(FormulaResult formulaResult, Concept concept) {
        // Lógica específica para aplicar la fórmula al concepto
        return formulaResult.getResult(); // Versión simplificada
    }
}