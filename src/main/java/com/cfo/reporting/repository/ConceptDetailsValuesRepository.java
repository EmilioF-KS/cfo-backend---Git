package com.cfo.reporting.repository;

import com.cfo.reporting.model.ConceptDetailValues;
import com.cfo.reporting.model.ConceptDetailValuesKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ConceptDetailsValuesRepository extends JpaRepository<ConceptDetailValues, ConceptDetailValuesKey> {
    @Query(value="SELECT * FROM tbl_cfo_column_details_values ccv " +
            "where ccv.concept_id = :conceptId " +
            "and ccv.gl_period = :glPeriod  " , nativeQuery=true)
    ConceptDetailValues  findByScreenConceptAndGlPeriod(
                         @Param("conceptId") long conceptId,
                         @Param("glPeriod") String glPeriod);

    @Query(value="SELECT * FROM tbl_cfo_column_details_values ccv " +
            " where ccv.concept_id = :conceptId " +
            " and ccv.gl_period = :glPeriod " +
            " and ccv.concept_detail_id = :conceptDetailId  ", nativeQuery=true)
    ConceptDetailValues findByScreenConceptDetailAndGlPeriod(
            @Param("conceptId") long conceptId,
            @Param("conceptDetailId") long concepDetailtId,
            @Param("glPeriod") String glPeriod);




}
