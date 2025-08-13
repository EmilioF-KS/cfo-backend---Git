package com.cfo.reporting.repository;


import com.cfo.reporting.model.Screen;
import jakarta.persistence.Entity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ScreenRepository extends JpaRepository<Screen,Long> {
    @EntityGraph(attributePaths = {})
    @Query("Select g from Screen g")
    List<Screen> findALlWithoutAssociations();
}
