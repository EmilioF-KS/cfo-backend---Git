package com.cfo.reporting.utils;

import com.cfo.reporting.dto.ConceptRecord;
import com.cfo.reporting.model.Concept;
import com.cfo.reporting.model.Formula;
import com.cfo.reporting.repository.ConceptRepository;
import com.cfo.reporting.repository.FormulaRepository;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class RecordsConceptsGenerator {
//    @Autowired
//    private ConceptRepository conceptRepository;
//
//    @Autowired
//    private FormulaRepository formulaRepository;
//
//    @Autowired
//    private KieSession kieSession;
//
//    public List<ConceptRecord> generarRegistros(String screenId) {
//        // 1. Obtener definiciones
//        List<Concept> conceptos = conceptRepository.allConceptsByScreenId(screenId);
//        //List<Formula> formulas = formulaRepository.(screenId);
//
//        // 2. Procesar cada concepto
//        return conceptos.stream().map(concepto -> {
//            RegistroConcepto registro = new RegistroConcepto();
//            registro.setDescripcion(concepto.getDescripcion());
//
//            formulas.forEach(formula -> {
//                Object valor = evaluarFormula(formula, concepto);
//                registro.agregarValor(
//                        formula.getNombreColumna(),
//                        valor,
//                        formula.getFormula(),
//                        formula.getTipo(),
//                        extraerTablaReferencia(formula.getFormula())
//                );
//            });
//
//            return registro;
//        }).collect(Collectors.toList());
//    }
//
//    private Object evaluarFormula(FormulaColumna formula, Concepto concepto) {
//        Map<String, Object> contexto = construirContexto(concepto);
//
//        // Insertar hechos en Drools
//        kieSession.insert(formula);
//        kieSession.insert(contexto);
//
//        // Crear y procesar evaluación
//        EvaluacionFormula evaluacion = new EvaluacionFormula(formula, contexto);
//        kieSession.insert(evaluacion);
//        kieSession.fireAllRules();
//
//        return evaluacion.getResultado();
//    }
//
//    private Map<String, Object> construirContexto(Concepto concepto) {
//        Map<String, Object> contexto = new HashMap<>();
//
//        // Agregar atributos del concepto
//        contexto.put("concepto", concepto.getCodigo());
//
//        // Agregar referencias a tablas
//        concepto.getReferencias().forEach((key, value) ->
//                contexto.put(key, obtenerValorDeTabla(value)));
//
//        return contexto;
//    }
}
