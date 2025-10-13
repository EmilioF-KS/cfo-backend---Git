package com.cfo.reporting.repository;


import com.cfo.reporting.model.ScreensReportCategory;
import com.cfo.reporting.model.ScreensReportCategoryKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ScreenRepCategoryRepository extends JpaRepository<ScreensReportCategory, ScreensReportCategoryKey> {

    @Query(value="Select * from tbl_cfo_screen_reports where reptype_id = :reptypeId", nativeQuery=true)
    List<ScreensReportCategory> allScreensByRepId(@Param("reptypeId") String reptypeId);
}
