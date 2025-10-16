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
public interface ScreenReportsRepository extends JpaRepository<ReptypeScreen, Long> {

    @Query(value="select * from tbl_cfo_screen_reptype " +
            "where reptype_id in (select reptype_id from tbl_cfo_screen_reports)", nativeQuery=true)
    List<ReptypeScreen> allReportsScreen() ;
}
