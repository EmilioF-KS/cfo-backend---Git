package com.cfo.reporting.repository;

import com.cfo.reporting.model.Concept;
import com.cfo.reporting.model.Header;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConceptRepository extends JpaRepository<Concept,Long> {
}
