package com.cfo.reporting.importing;

import java.util.List;

public interface BulkRepository {
    public void bulkInsert(String sqlStatement, List<Object[]> data);
}
