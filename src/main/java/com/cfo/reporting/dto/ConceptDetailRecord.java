package com.cfo.reporting.dto;

import com.cfo.reporting.utils.DynamicLookupProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record ConceptDetailRecord(String detailLabel
        , List<ColumnDetailRecord> allColumns,long detailId, int detilOrder) {

    public ConceptDetailRecord {
        allColumns = new ArrayList<>(allColumns);
    }

    public static ConceptDetailRecord fromDTOs(String detailLabel,
                                               List<ColumnDetailRecord> allColumns,
                                               long detailId,
                                               int detilOrder) {
        return new ConceptDetailRecord(
                detailLabel,
                allColumns,
                detailId,
                detilOrder

        );


    }

    public static void main(String[] args) {
        String formula = "ROUND(VLOOKUP(23000-2,gldays) - VLOOKUP(23000-5,gldays)))";
        //String regex = "(VLOOKUP|ROUND|[0-9]+(?:\\.[0-9]+)?(?:-[0-9]+)*|[a-zA-Z_][a-zA-Z0-9_]*|[-+*/^(),])";
        //String regex = "VLOOKUP\\\\s*\\\\(\\\\s*([^,]+)\\\\s*,\\\\s*([^)]+)\\\\s*\\\\)";
        //String regex = "(VLOOKUP\\\\s*\\\\(\\\\s*([^,]+)\\\\s*,\\\\s*([^)]+)\\\\s*\\\\))";
        String regex = "VLOOKUP\\s*\\(\\s*([^,]+)\\s*,\\s*([^)]+)\\s*\\)";
        Pattern pattern = Pattern.compile(regex,Pattern.CASE_INSENSITIVE);

        String result = formula;
        Matcher matcher = pattern.matcher(result);
        while(matcher.find()) {
            String antesDelVlookup = result.substring(0, matcher.start());
            if (!antesDelVlookup.toUpperCase().endsWith("ROUND")) {
                String key = matcher.group(1).trim();
                String table = matcher.group(2).trim();
                //Double value = buscarEnTabla(key, table);

//                if (value != null) {
//                    result = result.substring(0, matcher.start()) +
//                            value.toString() +
//                            result.substring(matcher.end());
//                    // Reiniciar el matcher con el nuevo resultado
//                    matcher = pattern.matcher(result);
//                }
            }
        }
    }
}