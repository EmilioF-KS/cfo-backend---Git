package com.cfo.reporting.importing;

import com.cfo.reporting.exception.DataProcessingException;
import com.cfo.reporting.model.UpdateTables;
import com.cfo.reporting.repository.UpdatedTablesRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public class ProcessingExcel implements ProcessExcellStrategy{

    private final UpdatedTablesRepository updatedTablesRepository;
    private final BulkRepositoryImpl bulkRepository;


    private static final int ACCOUNT_ID_COLUMN = 2;
    private static final int ACCOUNT_NUMBER_COLUMN = 5;


    public ProcessingExcel (BulkRepositoryImpl bulkRepository,
                                             UpdatedTablesRepository updatedTablesRepository) {
        this.bulkRepository = bulkRepository;
        this.updatedTablesRepository = updatedTablesRepository;
    }

    @Override
    public int processExcel(File file,String glPeriod) throws Exception {
        List<Object[]> rowData = new ArrayList<>();
        String table_name = this.getTableName(file);
         if (table_name == null) {
             throw new Exception("Not valid file to import"+file.getName());
         }
         //

         if (isAlreadyProcessed(table_name,glPeriod)) return -1;
         //
         StringBuilder stringBuilder = new StringBuilder();
        int  batchCount=0;
        try ( FileInputStream fis = new FileInputStream(file);
              Workbook workbook = new XSSFWorkbook(fis) ) {
                stringBuilder.append("INSERT INTO "+table_name+" VALUES (?,");
                Sheet sheet = workbook.getSheetAt(0);
                Iterator<Row> rowIterator = sheet.iterator();
                // Procesar encabezados
                Row headerRow = null;
                if (rowIterator.hasNext()) {
                    headerRow = rowIterator.next();
                }
                int columnas=0;
                stringBuilder.append(String.join(",", Collections.nCopies(headerRow.getLastCellNum(), "?")) + ")");
                int lastRow = getLastRowWithData((XSSFSheet) sheet);
                int currentRow = 1;
                int totalColumns=headerRow.getLastCellNum();
                while (lastRow >= 0) {
                    Row row = sheet.getRow(currentRow);
                    if (row == null) break;
                    Object[] rowColumns = new Object[totalColumns+1];
                    for (int i = 0; i < totalColumns ; i++) {
                        Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        System.out.println("Columna :"+i+cell.toString());
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
                                    rowColumns[columnas] = null;
                                } else {
                                    rowColumns[columnas] = cell.getStringCellValue();
                                }
                                break;
                            case NUMERIC:
                                if (columnas == ACCOUNT_ID_COLUMN || columnas == ACCOUNT_NUMBER_COLUMN) {
                                    rowColumns[columnas] = BigDecimal.valueOf(cell.getNumericCellValue()).toPlainString().replace(".0","");
                                } else if (DateUtil.isCellDateFormatted(cell)) {
                                    rowColumns[columnas]  =  new Date(cell.getDateCellValue().getTime());
                                } else {
                                    if (String.valueOf(cell.getNumericCellValue()).indexOf(".") > 0) {
                                        rowColumns[columnas] =cell.getNumericCellValue();
                                    } else {
                                        rowColumns[columnas] = cell.getNumericCellValue();
                                    }
                                }
                                break;
                            default:
                                rowColumns[columnas] =null;
                                break;
                        }
                        columnas++;
                    } // for
                    rowData.add(rowColumns);
                    columnas=0;
                    currentRow++;
                    lastRow--;
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
            }
        catch(Exception ex) {
           throw new DataProcessingException("Error when processing ExcelFile "+file.getName(),ex);
        }
        return batchCount;
    }

    private boolean isAlreadyProcessed(String tableName,String glPeriodo) {
        String queryCount = "Select count(*) from "+tableName+" where gl_period='"+glPeriodo+"'";
        long count = bulkRepository.recordsProcessedByTable(queryCount);
        return count > 0;
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


}
