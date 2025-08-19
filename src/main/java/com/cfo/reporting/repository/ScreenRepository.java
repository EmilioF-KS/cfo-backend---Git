package com.cfo.reporting.repository;


import com.cfo.reporting.model.Screen;
import jakarta.persistence.Entity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ScreenRepository extends JpaRepository<Screen,Long> {
//    @Query(value="Select * from Screen where screen_id = ?", nativeQuery=true)
//    Screen findByScreenId(@Param("screenId") String screenId);
}
