package com.cfo.reporting.repository;

import com.cfo.reporting.model.ConceptDetailFormula;
import com.cfo.reporting.model.ConceptDetailFormulaKey;
import com.cfo.reporting.model.DetailFormula;
import com.cfo.reporting.model.DetailFormulaKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ConceptDetailFormulaRepository extends JpaRepository<ConceptDetailFormula, ConceptDetailFormulaKey> {
    @Query(value="select * from tbl_cfo_column_formulas scf " +
            "where scf.concept_id = :concept_id ",nativeQuery = true)
    List<ConceptDetailFormula> allFormulaDetailsByConceptId(@Param("concept_id") long conceptId);
}
