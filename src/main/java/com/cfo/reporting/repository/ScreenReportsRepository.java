package com.cfo.reporting.repository;


import com.cfo.reporting.model.ReptypeScreen;
import com.cfo.reporting.model.ScreensReportCategory;
import com.cfo.reporting.model.ScreensReportCategoryKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScreenReportsRepository extends JpaRepository<ScreensReportCategory, ScreensReportCategoryKey> {
    @Query(value="select * from tbl_cfo_screen_reports " +
            "where screen_id=:screenId", nativeQuery=true)
    ScreensReportCategory reportsByScreenId(@Param("screenId") String screenId) ;
}
