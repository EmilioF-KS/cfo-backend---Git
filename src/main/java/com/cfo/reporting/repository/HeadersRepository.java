package com.cfo.reporting.repository;

import com.cfo.reporting.model.Concept;
import com.cfo.reporting.model.Header;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HeadersRepository extends JpaRepository<Header,Long> {

    @Query(value="Select * from tbl_cfo_screen_headers where screens_screen_id = :screenId", nativeQuery=true)
    List<Header> allHeadersByScreenId(@Param("screenId") String screenId);
}
