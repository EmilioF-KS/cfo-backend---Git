package com.cfo.reporting.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@Transactional
public class DynamicQueryService {

    @PersistenceContext
    private EntityManager entityManager;

    public static void main(String[] args) {
        List<Map<String,Object>> datos = new ArrayList<>();
        Map<String,Object> mapa1 = new TreeMap<>();
        mapa1.put("nombre","Maria");
        mapa1.put("edad",25);
        Map<String,Object> mapa2 = new HashMap<>();
        mapa2.put("nombre","Pedro");
        mapa2.put("edad",25);


        datos.add(mapa1);
        datos.add(mapa2);
        for (int i=0;i < datos.size();i++) {
            datos.get(i).put("orden",i+1);
        }

        System.out.println("List con orden "+datos);



    }

    /**
     * Ejecuta una consulta dinámica y retorna los resultados con comentarios como nombres de columna
     */
//    public List<Map<String, Object>> executeDynamicQueryWithComments(String dynamicQuery, String tableName) {
//        // Ejecutar la consulta dinámica
//        List<TreeMap<Integer,Map<String, Object>>> rawResults = null;
//        List<String> columnComments=null;
//        try {
//            rawResults = executeDynamicQuery(dynamicQuery, tableName);
//
//            // Obtener los comentarios de las columnas
//            columnComments = getColumnComments(tableName);
//        }catch (Exception ex) {
//            System.out.println(ex.getCause());
//        }
//
//        // Mapear los resultados con los comentarios como nombres de columna
//        return mapResultsWithComments(rawResults, columnComments);
//    }

    /**
     * Ejecuta una consulta dinámica y retorna los resultados crudos
     */
    public List<Map<String, Object>> executeDynamicQuery(String queryString,String tableName) {
        Query query = entityManager.createNativeQuery(queryString);

        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();

        // Obtener nombres de columnas
        List<String> columnNames = getColumnComments(tableName);
        List<Map<String, Object>> listFinalResult =  listResultsMap(convertToMapList(results, columnNames));
        return listFinalResult;
    }

    private List<Map<String,Object>> listResultsMap(List<TreeMap<Integer,Map<String,Object>>> listToConvert) {
        List<Map<String,Object>> listResults = new ArrayList<>();
        int index = 0;
        while (index < listToConvert.size()) {
            TreeMap<Integer,Map<String,Object>> treeToEmpty = listToConvert.get(index);
            Map<String,Object> unitedMap= new LinkedHashMap<>();
            while (!treeToEmpty.isEmpty()) {
                Map.Entry<Integer, Map<String, Object>> firstEntry = treeToEmpty.pollFirstEntry();
                unitedMap.putAll(firstEntry.getValue());
            }
            index++;
            listResults.add(unitedMap);
        }
        return listResults;
    }
    /**
     * Obtiene los comentarios de las columnas de una tabla
     */
    public List<String> getColumnComments(String tableName) {
        String schema = getCurrentSchema();

        String sql = "SELECT COLUMN_NAME, COLUMN_COMMENT " +
                "FROM INFORMATION_SCHEMA.COLUMNS " +
                "WHERE TABLE_NAME = ?  AND TABLE_SCHEMA = ?" +
                "order by ordinal_position";

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter(1, tableName);
        query.setParameter(2, schema);

        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();

        List<String> comments = new ArrayList<>();
        for (Object[] result : results) {
            //String columnName = (String) result[0];
            String comment = result[1] != null ? (String) result[1] : "No description ";
            comments.add(comment);
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
    private List<TreeMap<Integer,Map<String, Object>>> convertToMapList(List<Object[]> results, List<String> columnNames) {
        List<TreeMap<Integer,Map<String, Object>>> mappedResults = new ArrayList<>();

        for (Object[] row : results) {
            TreeMap<Integer,Map<String, Object>> treeColMap = new TreeMap<>();
            for (int i = 0; i < row.length && i < columnNames.size(); i++) {
                Map<String,Object> mapaRow = new HashMap<>();
                mapaRow.put(columnNames.get(i).toString().trim(), row[i]);
                treeColMap.put(i+1,mapaRow);
            }
            mappedResults.add(treeColMap);
        }

        return mappedResults;
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