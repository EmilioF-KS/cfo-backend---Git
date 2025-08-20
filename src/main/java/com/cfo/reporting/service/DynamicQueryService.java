package com.cfo.reporting.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class DynamicQueryService {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Ejecuta una consulta dinámica y retorna los resultados con comentarios como nombres de columna
     */
    public List<Map<String, Object>> executeDynamicQueryWithComments(String dynamicQuery, String tableName) {
        // Ejecutar la consulta dinámica
        List<Map<String, Object>> rawResults = executeDynamicQuery(dynamicQuery,tableName);

        // Obtener los comentarios de las columnas
        Map<String, String> columnComments = getColumnComments(tableName);

        // Mapear los resultados con los comentarios como nombres de columna
        return mapResultsWithComments(rawResults, columnComments);
    }

    /**
     * Ejecuta una consulta dinámica y retorna los resultados crudos
     */
    public List<Map<String, Object>> executeDynamicQuery(String queryString,String tableName) {
        Query query = entityManager.createNativeQuery(queryString);

        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();
        System.out.println("Longitud del results "+results.size());

        // Obtener nombres de columnas
        Map<String,String> columnNames = getColumnComments(tableName);
        //getColumnNamesFromQuery(queryString);
        List<String> listNames = new ArrayList<>(columnNames.values());

        // Convertir a List<Map<String, Object>>
        return convertToMapList(results, listNames);
    }

    /**
     * Obtiene los comentarios de las columnas de una tabla
     */
    public Map<String, String> getColumnComments(String tableName) {
        String schema = getCurrentSchema();

        String sql = "SELECT COLUMN_NAME, COLUMN_COMMENT " +
                "FROM INFORMATION_SCHEMA.COLUMNS " +
                "WHERE TABLE_NAME = ? AND TABLE_SCHEMA = ?";

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter(1, tableName);
        query.setParameter(2, schema);

        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();

        Map<String, String> comments = new HashMap<>();
        for (Object[] result : results) {
            String columnName = (String) result[0];
            String comment = result[1] != null ? (String) result[1] : columnName;
            comments.put(columnName.toUpperCase(), comment);
        }

        return comments;
    }

    /**
     * Obtiene el schema actual de la base de datos
     */
    private String getCurrentSchema() {
        Query query = entityManager.createNativeQuery("SELECT DATABASE()");
        return (String) query.getSingleResult();
    }

    /**
     * Extrae nombres de columnas aproximados de la consulta SQL
     */
    private List<String> getColumnNamesFromQuery(String queryString) {
        // Esta es una simplificación - en producción deberías parsear mejor el SQL
        String upperQuery = queryString.toUpperCase();

        if (upperQuery.contains("SELECT * FROM")) {
            // Si es SELECT *, necesitarías obtener todas las columnas de la tabla
            return getallColumnNamesFromTable(queryString);
        }

        // Extraer nombres entre SELECT y FROM
        int selectIndex = upperQuery.indexOf("SELECT") + 6;
        int fromIndex = upperQuery.indexOf("FROM");

        if (selectIndex > 0 && fromIndex > selectIndex) {
            String columnsPart = queryString.substring(selectIndex, fromIndex).trim();
            String[] columns = columnsPart.split(",");

            List<String> columnNames = new ArrayList<>();
            for (String column : columns) {
                // Remover aliases y funciones
                String cleanColumn = column.trim()
                        .replaceAll("(?i)AS.*", "")
                        .replaceAll("[^a-zA-Z0-9_]", "")
                        .trim();

                if (!cleanColumn.isEmpty()) {
                    columnNames.add(cleanColumn);
                }
            }
            return columnNames;
        }

        return new ArrayList<>();
    }

    /**
     * Convierte resultados a List<Map<String, Object>>
     */
    private List<Map<String, Object>> convertToMapList(List<Object[]> results, List<String> columnNames) {
        List<Map<String, Object>> mappedResults = new ArrayList<>();

        for (Object[] row : results) {
            Map<String, Object> rowMap = new HashMap<>();
            for (int i = 0; i < row.length && i < columnNames.size(); i++) {
                rowMap.put(columnNames.get(i), row[i]);
            }
            mappedResults.add(rowMap);
        }

        return mappedResults;
    }

    /**
     * Mapea resultados usando comentarios como nombres de columna
     */
    private List<Map<String, Object>> mapResultsWithComments(
            List<Map<String, Object>> rawResults,
            Map<String, String> columnComments) {

        List<Map<String, Object>> finalResults = new ArrayList<>();

        for (Map<String, Object> rawRow : rawResults) {
            Map<String, Object> finalRow = new HashMap<>();

            for (Map.Entry<String, Object> entry : rawRow.entrySet()) {
                String columnName = entry.getKey();
                String displayName = columnComments.getOrDefault(
                        columnName.toUpperCase(), columnName);

                finalRow.put(displayName, entry.getValue());
            }

            finalResults.add(finalRow);
        }

        return finalResults;
    }

    /**
     * Método auxiliar para obtener nombres de columnas cuando se usa SELECT *
     */
    private List<String> getallColumnNamesFromTable(String queryString) {
        // Implementación para extraer nombres de tabla y obtener columnas
        // Esto es más complejo y requeriría un parser de SQL más sofisticado
        return new ArrayList<>();
    }
}