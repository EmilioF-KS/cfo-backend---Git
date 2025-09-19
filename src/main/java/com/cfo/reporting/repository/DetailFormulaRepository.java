package com.cfo.reporting.repository;

import com.cfo.reporting.model.ConceptDetailValues;
import com.cfo.reporting.model.DetailFormula;
import com.cfo.reporting.model.DetailFormulaKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DetailFormulaRepository extends JpaRepository<DetailFormula, DetailFormulaKey> {
    @Query(value="select * from tbl_cfo_column_formulas_details scf " +
            "where scf.concept_detail_id = :detail_id ",nativeQuery = true)
    List<DetailFormula> allFormulaDetailsById(@Param("detail_id") int conceptId);
}
