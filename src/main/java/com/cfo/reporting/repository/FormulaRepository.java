package com.cfo.reporting.repository;


import com.cfo.reporting.model.Formula;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FormulaRepository extends JpaRepository<Formula,Long> {

    //@Query(value="Select * from tbl_cfo_column_formulas where ")
}
