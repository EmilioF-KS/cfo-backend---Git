package com.cfo.reporting.importing;

import com.cfo.reporting.exception.DataProcessingException;
import com.cfo.reporting.model.UpdateTables;
import com.cfo.reporting.repository.UpdatedTablesRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProcessingExcelGLDAYS {

    BulkRepositoryImpl bulkRepository;
    UpdatedTablesRepository updatedTablesRepository;

    private static final int ACCOUNT_ID_COLUMN = 2;
    private static final int ACCOUNT_NUMBER_COLUMN = 5;


    public int importGLDAYSExcelFile(File excellFile,String glPeriod) throws Exception {

        List<Object[]> rowData = new ArrayList<>();
        String table_name = this.getTableName(excellFile);
        if (table_name == null) {
            throw new Exception("Not valid file to import"+excellFile.getName());
        }
        //

        if (isAlreadyProcessed(table_name,glPeriod)) return -1;
        StringBuilder stringBuilder= new StringBuilder();
        int columnas=0;
        int  batchCount=0;
        int rownumber = 1;
            try (
                FileInputStream fis = new FileInputStream(excellFile);
                Workbook workbook = new XSSFWorkbook(fis);
            ) {

            stringBuilder.append("INSERT INTO "+table_name+" VALUES (?,");
            Sheet sheet = workbook.getSheetAt(0);
            // Procesar encabezados
            int currentRow = 6;
            Row headerRow = sheet.getRow(currentRow);

            stringBuilder.append(String.join(",", Collections.nCopies(headerRow.getLastCellNum(), "?")) + ")");
            // Procesar datos
            int lastRow = getLastRowWithData((XSSFSheet) sheet);
            int totalRows = lastRow;
            currentRow+=3;
            Cell cellValidation=null;

            while (lastRow >= 0) {
                Row row = sheet.getRow(currentRow);
                if (row == null && currentRow >= totalRows) {
                    break;
                } else if(row == null && currentRow <= totalRows) {
                    currentRow++;
                    lastRow--;
                    row=null;
                    continue;
                }
                cellValidation = row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                int totalColumns =  headerRow.getLastCellNum()+1;
                if (cellValidation.getCellType() == CellType.BLANK) {
                    currentRow++;
                    lastRow--;
                    row=null;
                    continue;
                } else if (cellValidation.getCellType() == CellType.STRING) {
                    if (!row.getCell(0).getStringCellValue().contains("-") ) {
                        currentRow++;
                        lastRow--;
                        row=null;
                        continue;
                    }
                }
                Object[] rowColumns = new Object[totalColumns];
                for (int i = 0; i < totalColumns; i++) {
                    Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    if (i==0){
                        rowColumns[0]=glPeriod;
                        columnas++;
                    }
                    if (columnas >= totalColumns) {
                        columnas=rowColumns.length -1;
                    }
                    switch (cell.getCellType()) {
                        case STRING:
                            int foundSlash = cell.getStringCellValue().indexOf("/");
                            if (foundSlash == 0 || foundSlash > 0   ) {
                                rowColumns[columnas]=null;
                            } else {
                                rowColumns[columnas]=cell.getStringCellValue();
                            }
                            break;
                        case NUMERIC:
                            if (columnas == ACCOUNT_ID_COLUMN || columnas == ACCOUNT_NUMBER_COLUMN) {
                                rowColumns[columnas]=BigDecimal.valueOf(cell.getNumericCellValue()).toPlainString().replace(".0","");
                            } else if (DateUtil.isCellDateFormatted(cell)) {
                                rowColumns[columnas]= new Date(cell.getDateCellValue().getTime());
                            } else {
                                if (String.valueOf(cell.getNumericCellValue()).indexOf(".") > 0) {
                                    rowColumns[columnas]=cell.getNumericCellValue();
                                } else {
                                    rowColumns[columnas]=cell.getNumericCellValue();
                                }
                            }
                            break;
                        case BLANK:
                            rowColumns[columnas]=null;
                            break;
                        default:
                            break;
                    }
                    columnas++;
                    System.out.println("Columna "+columnas+" "+rowColumns[i]);
                } // for
                columnas=0;
                currentRow++;
                rownumber++;
                lastRow--;
                rowData.add(rowColumns);
                if (++batchCount % 1000 == 0) {
                    System.out.println("Guardando registros ");
                    bulkRepository.bulkInsert(stringBuilder.toString(),rowData);
                    rowData.clear();
                }
            } // while
            if (rowData.size() > 0) {
                bulkRepository.bulkInsert(stringBuilder.toString(),rowData);
            }
        }
            catch (FileNotFoundException e) {
                throw new RuntimeException(e);
        } catch (IOException e) {
                throw new RuntimeException(e);
        } catch(Exception ex) {
                throw new DataProcessingException("Exception in processing file "+excellFile.getName()+ ex.getMessage());
        }
    return batchCount;
  }
    private String getTableName(File file) {
        List<UpdateTables> updateTablesList = updatedTablesRepository.findAll();
        String fileName = file.getName();
        Optional<String> tableName = buscarEntidad(
                updateTablesList,
                e -> fileName.toLowerCase().contains(e.getTable_alias()) ,
                UpdateTables::getTable_name
        );

        if (tableName.isPresent()) {
            return tableName.get();
        }
        else
            return null;
    }

    public static <T, R> Optional<R> buscarEntidad(List<T> entidades,
                                                   Predicate<T> criterio,
                                                   Function<T, R> extractor) {
        return entidades.stream()
                .filter(criterio)
                .findFirst()
                .map(extractor);
    }
    private int getLastRowWithData(XSSFSheet sheet) {
        int lastRow = sheet.getLastRowNum();
        while (lastRow >=0 ){
            if (sheet.getRow(lastRow) != null && !isRowEmpty(sheet.getRow(lastRow))) {
                break;
            }
            lastRow--;
        }
        return lastRow;
    }
    private boolean isRowEmpty(XSSFRow row) {
        if (row == null) return true;
        for (int c= row.getFirstCellNum(); c< row.getLastCellNum(); c++){
            Cell  cell= row.getCell(c);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                return false;
            }
        }
        return true;
    }

    private boolean isAlreadyProcessed(String tableName,String glPeriodo) {
        String queryCount = "Select count(*) from "+tableName+" where gl_period='"+glPeriodo+"'";
        long count = bulkRepository.recordsProcessedByTable(queryCount);
        return count > 0;
    }
}
