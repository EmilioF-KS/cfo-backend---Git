package com.cfo.reporting.config;

public interface ApplicationConstants {
    String queryHasSubconcepts = "select count(*) from tbl_cfo_screen_concepts scc " +
            "left join tbl_cfo_concept_details scd on scd.concept_id = scc.concept_id ";
}
