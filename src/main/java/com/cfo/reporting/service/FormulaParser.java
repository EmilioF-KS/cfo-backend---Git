package com.cfo.reporting.service;

import java.util.Arrays;
import java.util.List;

public class FormulaParser {
    public static ParsedFormula parse(String formula) {
        // Implementación de análisis sintáctico
        return new ParsedFormula(formula);
    }

    public static class ParsedFormula {
        private final String original;
        private final List<String> components;

        public ParsedFormula(String original) {
            this.original = original;
            this.components = parseComponents(original);
        }

        private List<String> parseComponents(String formula) {
            // Lógica para extraer componentes (VLOOKUP, variables, operadores)
            return Arrays.asList(formula.split("(?=[+\\-*/()])|(?<=[+\\-*/()])"));
        }

        public boolean containsVLookup() {
            return original.contains("VLOOKUP");
        }
    }
}