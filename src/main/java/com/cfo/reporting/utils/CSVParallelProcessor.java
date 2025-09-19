package com.cfo.reporting.utils;

import com.cfo.reporting.model.CsvExporting;
import com.cfo.reporting.model.CsvExportingKey;
import com.cfo.reporting.service.BackgroundSaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Component
public class CSVParallelProcessor {

    @Autowired
    private DataSource dataSource;

    public List<CsvExporting> processToGenerateCSV(String glPeriod) throws InterruptedException, ExecutionException {
        List<CsvExporting> allCsvExportingRecors = new ArrayList<>();
        return allCsvExportingRecors = processRows(glPeriod);
    }

    private List<CsvExporting> processRows(String glPeriod) {

        List<CsvExporting> allProcessedRows = new ArrayList<>();
        try (Connection conn = dataSource.getConnection()) {
            // Configurar nivel de aislamiento y auto-commit
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            conn.setAutoCommit(false);
            String query = "SELECT " +
                    "    t.*, " +
                    "    COALESCE( " +
                    "        (SELECT column_value FROM tbl_cfo_column_details_values " +
                    "         WHERE concept_id = t.concept_id AND concept_detail_id = t.detai_id " +
                    "         AND gl_period = ?" +
                    "         LIMIT 1)," +
                    "        (SELECT column_value FROM tbl_cfo_column_details_values " +
                    "         WHERE concept_id = t.concept_id AND detai_id = 0 " +
                    "         AND gl_period = ? " +
                    "         LIMIT 1), " +
                    "        0 " +
                    "    ) AS valor " +
                    " FROM tbl_cfo_csv_exporting_template t ";

            try (PreparedStatement selectStmt = conn.prepareStatement(query)) {
                selectStmt.setString(1, glPeriod);
                selectStmt.setString(2, glPeriod);
                try (ResultSet rs = selectStmt.executeQuery()) {
                    // Procesar el ResultSet inmediatamente dentro del mismo contexto
                    while (rs.next()) {
                        CsvExporting csvExporting = new CsvExporting();
                        CsvExportingKey csvExportingKey = new CsvExportingKey();
                        csvExportingKey.setGlPeriod(rs.getString("gl_period"));
                        csvExportingKey.setIndicatorId(rs.getString("indicator_id"));
                        csvExportingKey.setFormId(rs.getString("form_id"));
                        csvExporting.setId(csvExportingKey);
                        csvExporting.setEntityId(rs.getString("entity_id"));
                        csvExporting.setReportAmount(rs.getDouble("valor"));
                        csvExporting.setStatusCd("P");
                        allProcessedRows.add(csvExporting);
                    }
                }
            }
            return allProcessedRows;
        } catch (SQLException e) {
            System.out.println("SQLExceltion when retrieving data "+e.getMessage());
            return new ArrayList<>();
        }
    }



    public static void main(String[] args) {
        try {
            CSVParallelProcessor processor = new CSVParallelProcessor();
            long startTime = System.currentTimeMillis();

            processor.processToGenerateCSV("202502");

            long endTime = System.currentTimeMillis();
            System.out.println("Procesamiento completado en " + (endTime - startTime) + " ms");

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private Date getDateFromPeriod(String glPeriod) {

        LocalDate dateReturn = null;
        Date returnDate = null;
        try {
            YearMonth yearPeriod = YearMonth.parse(glPeriod, DateTimeFormatter.ofPattern("yyyyMM"));
            dateReturn = yearPeriod.atDay(1);
            returnDate = new Date(Date.from(dateReturn.atStartOfDay(ZoneId.of("UTC")).toInstant()).getTime());
        }
        catch(DateTimeParseException ex) {
            System.out.println("Invalid date "+glPeriod);
        }
        catch (Exception ex) {
            System.out.println("Error :"+ex.getMessage());
        }
        return returnDate;
    }



}
