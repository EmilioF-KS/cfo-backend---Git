package com.cfo.reporting.config;

public interface ApplicationConstants {
    String queryHasSubconcepts = "select count(*) from tbl_cfo_screen_concepts where " +
            " screen_concepts_concept_id > 0 ";
}
