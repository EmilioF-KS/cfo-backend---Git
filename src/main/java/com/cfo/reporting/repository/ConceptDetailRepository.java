package com.cfo.reporting.repository;

import com.cfo.reporting.dto.DetailFormulaResult;
import com.cfo.reporting.model.ConceptDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ConceptDetailRepository extends JpaRepository<ConceptDetail,Long> {
    @Query(value="select detail_value,detail_label,formula_text,column_name from tbl_cfo_screens sc, " +
            "tbl_cfo_screen_concepts scc, tbl_cfo_concept_details scd " +
            "where sc.screen_id = scc.screen_id " +
            "and scc.concept_id = scd.concept_id " +
            "and sc.screen_id = :screenId " +
            "and scc.concept_id = :conceptId",nativeQuery = true)
    List<DetailFormulaResult> allDetailsByScreenAndConcept(@Param("screenId") String screenId,
                                                           @Param("conceptId") int conceptId);

    @Query(value="select scd.detail_id,scd.concept_id,scd.detail_value,scd.detail_label,scd.detail_order from tbl_cfo_concept_details scd, " +
            " tbl_cfo_screen_concepts scc " +
            " where scc.concept_id = scd.concept_id " +
            " and scc.concept_id = :conceptId",nativeQuery = true)
    List<ConceptDetail> allDetailsByConcept(@Param("conceptId") int conceptId);
}
