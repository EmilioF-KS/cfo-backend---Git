package com.cfo.reporting.config;

public interface ApplicationConstants {
    String queryHasSubconcepts = "select count(*) from tbl_cfo_screen_concepts scc " +
            "where query_concepts is not null" ;
}
