package com.cfo.reporting.repository;

import com.cfo.reporting.model.Concept;
import com.cfo.reporting.model.CsvExporting;
import com.cfo.reporting.model.CsvExportingKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CsvExportingRepository extends JpaRepository<CsvExporting, CsvExportingKey> {

    @Query(value="SELECT * FROM tbl_cfo_csv_exporting\n" +
            "where gl_period = :glPeriod" +
            " and form_id = :formId " , nativeQuery=true)
    List<CsvExporting> allCsvRecordsToExport(@Param("glPeriod") String glPeriod,
                                              @Param("formId") String formId);

}
