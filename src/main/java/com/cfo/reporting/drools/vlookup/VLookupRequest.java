package com.cfo.reporting.drools.vlookup;


public class VLookupRequest {
    private final Object lookupValue;
    private final String tableName;
    private final String columnName;
    private final VLookupExpression.MatchType matchType;
    private final VLookupExpression.RangeMode rangeMode;

    public enum MatchType {
        EXACT, APPROXIMATE, WILDCARD
    }

    public enum RangeMode {
        FIRST, LAST, ALL
    }

    // Constructor completo
    public VLookupRequest(Object lookupValue, String tableName,
                          String columnName, VLookupExpression.MatchType matchType,
                          VLookupExpression.RangeMode rangeMode) {
        this.lookupValue = lookupValue;
        this.tableName = tableName;
        this.columnName = columnName;
        this.matchType = matchType;
        this.rangeMode = rangeMode;
    }

    // Constructor simplificado (para casos comunes)
    public VLookupRequest(Object lookupValue, String tableName, String columnName) {
        this(lookupValue, tableName, columnName, VLookupExpression.MatchType.EXACT, VLookupExpression.RangeMode.FIRST);
    }

    // Constructor con tipo de coincidencia
    public VLookupRequest(Object lookupValue, String tableName,
                          String columnName, VLookupExpression.MatchType matchType) {
        this(lookupValue, tableName, columnName, matchType, VLookupExpression.RangeMode.FIRST);
    }

    // Getters
    public Object getLookupValue() { return lookupValue; }
    public String getTableName() { return tableName; }
    public String getColumnName() { return columnName; }
    public VLookupExpression.MatchType getMatchType() { return matchType; }
    public VLookupExpression.RangeMode getRangeMode() { return rangeMode; }

    @Override
    public String toString() {
        return String.format("VLookupRequest{value=%s, table=%s, column=%s, match=%s, range=%s}",
                lookupValue, tableName, columnName, matchType, rangeMode);
    }
}