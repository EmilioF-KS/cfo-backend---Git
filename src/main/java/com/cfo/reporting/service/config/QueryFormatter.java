package com.cfo.reporting.service.config;

import java.util.Date;
import java.util.Map;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueryFormatter {
    private static final Pattern PARAM_PATTERN = Pattern.compile(":(\\w+)");

    public static String formatQuery(String query, Map<String, Object> parametros) {
        if (parametros == null || parametros.isEmpty()) {
            return query;
        }

        StringBuffer result = new StringBuffer();
        Matcher matcher = PARAM_PATTERN.matcher(query);

        while (matcher.find()) {
            String paramName = matcher.group(1);
            Object paramValue = parametros.get(paramName);

            if (paramValue != null) {
                String replacement;

                if (paramValue instanceof String) {
                    replacement = "'" + escapeSql(paramValue.toString()) + "'";
                } else if (paramValue instanceof List) {
                    // Manejar listas para cláusulas IN
                    replacement = formatListForInClause((List<?>) paramValue);
                } else if (paramValue instanceof Date) {
                    replacement = "'" + new java.sql.Date(((Date) paramValue).getTime()) + "'";
                } else {
                    replacement = paramValue.toString();
                }

                matcher.appendReplacement(result, replacement);
            } else {
                // Si el parámetro no está presente, dejar el marcador como está
                matcher.appendReplacement(result, matcher.group(0));
            }
        }

        matcher.appendTail(result);
        return result.toString();
    }

    private static String formatListForInClause(List<?> list) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) {
                sb.append(",");
            }

            Object item = list.get(i);
            if (item instanceof String) {
                sb.append("'").append(escapeSql(item.toString())).append("'");
            } else {
                sb.append(item.toString());
            }
        }
        return sb.toString();
    }

    private static String escapeSql(String str) {
        if (str == null) {
            return "";
        }
        return str.replace("'", "''");
    }

    public static void validateQuery(String query) {
        // Validaciones básicas de la query
        if (query == null || query.trim().isEmpty()) {
            throw new IllegalArgumentException("La query no puede estar vacía");
        }

        // Verificar que no tenga puntos y coma múltiples (posible inyección SQL)
        if (query.split(";").length > 2) {
            throw new IllegalArgumentException("La query contiene múltiples statements, lo cual no está permitido");
        }

        // Verificar palabras clave peligrosas (simplificado)
        String upperQuery = query.toUpperCase();
        if (upperQuery.contains("DROP TABLE") ||
                upperQuery.contains("DELETE FROM") ||
                upperQuery.contains("UPDATE ") ||
                upperQuery.contains("INSERT INTO")) {
            throw new IllegalArgumentException("La query contiene operaciones no permitidas");
        }
    }
}