package com.cfo.reporting.drools.vlookup;


import com.cfo.reporting.drools.vlookup.model.data.EvaluationContext;
import lombok.Data;

/**
 * Represents a VLOOKUP expression in a formula.
 * Actual lookup logic is handled in Drools rules.
 */
@Data
public class VLookupExpression implements ExpressionPart {

    private final ExpressionPart lookupValue;
    private final String tableName;
    private final String columnName;
    private final MatchType matchType;
    private final RangeMode rangeMode;

    public enum MatchType { EXACT, APPROXIMATE, WILDCARD }
    public enum RangeMode { FIRST, LAST, ALL }

    public VLookupExpression(ExpressionPart lookupValue, String tableName,
                             String columnName, MatchType matchType, RangeMode rangeMode) {
        this.lookupValue = lookupValue;
        this.tableName = tableName;
        this.columnName = columnName;
        this.matchType = matchType;
        this.rangeMode = rangeMode;
    }

    @Override
    public String getText() {
        return String.format("VLOOKUP(%s, '%s', '%s', %s, %s)",
                lookupValue.getText(), tableName, columnName, matchType, rangeMode);
    }

    @Override
    public Object evaluate(EvaluationContext context) {
        // Evaluate the lookupValue in the given context
        Object valueToLookup = lookupValue.evaluate(context);
        // Actual lookup is handled in Drools rules
        return valueToLookup;
    }

    @Override
    public ExpressionPartType getType() {
        return ExpressionPartType.VLOOKUP;
    }

    // Explicit getters (Lombok @Data already generates these, optional)
    public ExpressionPart getLookupValue() { return lookupValue; }
    public String getTableName() { return tableName; }
    public String getColumnName() { return columnName; }
    public MatchType getMatchType() { return matchType; }
    public RangeMode getRangeMode() { return rangeMode; }
}
