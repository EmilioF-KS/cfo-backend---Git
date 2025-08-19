package com.cfo.reporting.drools.vlookup;

import java.util.HashMap;
import java.util.Map;

public class ReferenceDataLoader {

    // Storage for reference tables
    private final Map<String, Map<String, Map<String, Object>>> tables = new HashMap<>();

    /**
     * Add a reference data table.
     *
     * @param tableName Name of the table (e.g. "products")
     * @param tableData Map<rowKey, Map<columnName, value>>
     */
    public void addTable(String tableName, Map<String, Map<String, Object>> tableData) {
        tables.put(tableName, tableData);
    }

    /**
     * Look up a value in the reference table.
     */
    public Object lookup(String tableName, String key, String columnName) {
        Map<String, Map<String, Object>> table = tables.get(tableName);
        if (table == null) {
            System.err.println("No table found: " + tableName);
            return null;
        }

        Map<String, Object> row = table.get(key);
        if (row == null) {
            System.err.println("No row found for key: " + key);
            return null;
        }

        return row.get(columnName);
    }

    /**
     * Preload sample reference data for Drools evaluation.
     * Replace this with DB calls or API calls in production.
     */
    public void loadReferenceData() {
        // Example: product table
        Map<String, Map<String, Object>> productTable = Map.of(
                "P-100", Map.of("price", 50.0, "name", "Product A"),
                "P-200", Map.of("price", 75.0, "name", "Product B"),
                "P-300", Map.of("price", 120.0, "name", "Product C")
        );
        Map<String, Map<String, Object>> newProds = Map.of(
                "P-100", Map.of("price", 50.0, "name", "Product A"),
                "P-200", Map.of("price", 75.0, "name", "Product B"),
                "P-300", Map.of("price", 120.0, "name", "Product C")
        );

        addTable("products", productTable);
        addTable("newprods", newProds);


        System.out.println("Reference data loaded: " + tables.keySet());
    }
}
