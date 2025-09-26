package com.cfo.reporting.service;


import com.cfo.reporting.model.CsvExporting;
import com.cfo.reporting.repository.CsvExportingRepository;
import com.cfo.reporting.repository.ScreenRepository;
import com.opencsv.CSVWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;

@Service
public class CsvBlobService {

    @Autowired
    ScreenRepository screenRepository;
    @Autowired
    CsvExportingRepository csvExportingRepository;
    private static String[] colsCsv = {"entity_id","status_cd","form_id","reporting_period","indicator_id","report_amount"};

    public Blob generateCsvCFO(String glPeriod, String screenId) throws IOException {

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
             CSVWriter csvWriter = new CSVWriter(writer))
        {
            String formId = screenRepository.findByScreenId(screenId).getScreen_formid();
            csvWriter.writeNext(colsCsv);
            List<CsvExporting> allCsvToExport = csvExportingRepository.allCsvRecordsToExport(glPeriod,formId);
            for( CsvExporting csvExporting: allCsvToExport) {
                csvWriter.writeNext(new String[] {
                        csvExporting.getEntityId(),
                        csvExporting.getStatusCd(),
                        csvExporting.getId().getFormId(),
                        csvExporting.getId().getGlPeriod(),
                        csvExporting.getId().getIndicatorId(),
                        String.valueOf(csvExporting.getReportAmount())
                });
            }
            csvWriter.flush();
            return new SerialBlob(outputStream.toByteArray());
        }
        catch(IOException ioex) {
           throw new RuntimeException("Error when generating CSV");
        } catch (SerialException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
