package com.cfo.reporting.repository;

import com.cfo.reporting.dto.DetailFormulaResult;
import com.cfo.reporting.model.Concept;
import com.cfo.reporting.model.Header;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ConceptRepository extends JpaRepository<Concept,Long> {

    @Query(value="Select * from tbl_cfo_screen_concepts where screen_id = :screenId", nativeQuery=true)
    List<Concept> allConceptsByScreenId(@Param("screenId") String screenId);

    @Query(value="Select * from tbl_cfo_screen_concepts where screen_id = :screenId" +
            " and concept_id = :conceptId", nativeQuery=true)
    Concept allConceptsByScreenIdConceptId(@Param("screenId") String screenId,
                                                          @Param("conceptId") int conceptId);

    @Query(value="Select * from tbl_cfo_screen_concepts where screen_id = :screenId" +
            " and screen_concepts_concept_id = :conceptId " +
            " order by concept_order", nativeQuery=true)
    Concept allSubConceptsByScreenIdConceptId(@Param("screenId") String screenId,
                                           @Param("conceptId") int conceptId);

    @Query(value="Select * from tbl_cfo_screen_concepts where screen_id = :screenId" +
            " and screen_concepts_concept_id = :conceptId " +
            " order by concept_order", nativeQuery=true)
    List<Concept> allSubConceptsByConceptId(@Param("screenId") String screenId,
                                              @Param("conceptId") int conceptId);


    @Query(value="select * from tbl_cfo_screen_concepts where screen_id = :screenId " +
            " and screen_concepts_concept_id = 0"  +
            " order by concept_order", nativeQuery=true)
    List<Concept> allParentConcepts(@Param("screenId") String screenId);
}
