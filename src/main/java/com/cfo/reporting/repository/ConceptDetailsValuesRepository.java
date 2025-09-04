package com.cfo.reporting.repository;

import com.cfo.reporting.dto.ConceptDetailValuesDTO;
import com.cfo.reporting.model.ConceptDetailValueKey;
import com.cfo.reporting.model.ConceptDetailValues;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ConceptDetailsValuesRepository extends JpaRepository<ConceptDetailValues, ConceptDetailValueKey> {
    @Query(value="SELECT ccv.column_value_id, sc.concept_id,ccv.concept_detail_id , ccv.column_name,ccv.column_value FROM tbl_cfo_screen_concepts sc, tbl_cfo_concept_details cd, tbl_cfo_column_details_values ccv " +
            "where sc.screen_id = cd.concept_id " +
            "and cd.concept_id = ccv.concept_id " +
            " and ccv.concept_detail_id is null " +
            "and sc.screen_id =  :screenId " +
            "and ccv.gl_period = :glPeriod " +
            "and sc.concept_id = :conceptId ", nativeQuery=true)
    ConceptDetailValuesDTO findByScreenConceptAndGlPeriod(
                                         @Param("screenId") String screenId,
                                         @Param("conceptId") long conceptId,
                                         @Param("glPeriod") String glPeriod);

    @Query(value="SELECT ccv.column_value_id, sc.concept_id,ccv.concept_detail_id, ccv.column_name,ccv.column_value FROM tbl_cfo_screen_concepts sc, tbl_cfo_concept_details cd, tbl_cfo_column_details_values ccv " +
            "where sc.screen_id = cd.concept_id " +
            " and cd.concept_id = ccv.concept_id " +
            " and sc.concept_id = :conceptId " +
            " and sc.screen_id =  :screenId " +
            " and ccv.gl_period = :glPeriod " +
            " and ccv.concept_detail_id = :concepDetailtId ", nativeQuery=true)

    ConceptDetailValuesDTO findByScreenConceptDetailAndGlPeriod(
            @Param("screenId") String screenId,
            @Param("conceptId") long conceptId,
            @Param("conceptDetailId") long concepDetailtId,
            @Param("glPeriod") String glPeriod);



}
