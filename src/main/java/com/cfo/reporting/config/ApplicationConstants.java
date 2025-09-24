package com.cfo.reporting.config;

public interface ApplicationConstants {
    String queryHasSubconcepts = "select count(*) from tbl_cfo_screen_concepts scc, tbl_cfo_concept_details scd " +
            "where scc.concept_id = scd.concept_id  ";
}
