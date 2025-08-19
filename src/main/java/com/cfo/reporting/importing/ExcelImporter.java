package com.cfo.reporting.importing;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;

public class ExcelImporter {
    private static final String DB_URL= "jdbc:derby:cfo-reporting;create= true";
    private static final String TABLE_NAME_PREFIX= "TBL_CFO_";
    private static final int ACCOUNT_ID_COLUMN = 2;
    private static final int ACCOUNT_NUMBER_COLUMN = 5;
    private static final SimpleDateFormat sdfValidator = new SimpleDateFormat("MM/DD/YYYY");

    public static ImportResult importExcelFile(File file) throws IOException {
        List<Object[]> data = new ArrayList<>();
        String[] headers = null;
        String tableName = generateTableName(file.getName());
        StringBuilder stringBuilder= new StringBuilder();

        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            stringBuilder.append("CREATE TABLE "+tableName+" (" +
                    " gl_period date not null, ");
            // Procesar encabezados
            if (rowIterator.hasNext()) {
                Row headerRow = rowIterator.next();
                headers = new String[headerRow.getLastCellNum()];

                for (int i = 0; i < headers.length; i++) {
                    Cell cell = headerRow.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    headers[i] = cell.toString().trim().replaceAll("[/.+,\\s-]","_").toLowerCase();
                }
                System.out.println("Numero de columnas :"+ Arrays.toString(headers));
            }

            // Procesar datos
            //while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Object[] rowData = new Object[headers.length];
                int lenField=0;
                for (int i = 0; i < headers.length; i++) {
                    Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);

                    switch (cell.getCellType()) {
                        case STRING:
                            if ( cell.getStringCellValue().length() > 10) {
                                lenField = 80;
                            } else {
                                lenField = cell.getStringCellValue().length();
                            }

                            stringBuilder.append(headers[i]+" VARCHAR("+lenField+"),");
                            break;
                        case NUMERIC:
                            if (DateUtil.isCellDateFormatted(cell)) {
                                stringBuilder.append(headers[i]+" Date, ");
                                rowData[i] = cell.getDateCellValue();
                            } else {
                                if (String.valueOf(cell.getNumericCellValue()).indexOf(".") > 0 ) {
                                    stringBuilder.append(headers[i] + " double, ");
                                } else {
                                    stringBuilder.append(headers[i] + " long, ");
                                }
                            }
                            break;
                        case BOOLEAN:
                            break;
                        case FORMULA:
                            System.out.println("La formula "+cell.getCellFormula());
                            break;
                        default:
                            stringBuilder.append(headers[i]+" VARCHAR(10), ");
                            break;
                    }
                }
            stringBuilder.append(" PRIMARY KEY(gl_period,"+headers[0]+" ) );");
            //    data.add(rowData);
            //}
            System.out.println(" MDL "+stringBuilder.toString().replaceAll("#","id"));
        }


        return new ImportResult(tableName, headers, data, file.getName());
    }



    public static int insertTableValues(File excellFile) throws FileNotFoundException,
            SQLException, IOException {
        StringBuilder stringBuilder= new StringBuilder();
        int batchCount = 0;
        int columnas=0;
        int rownumber = 1;
        int columnerr=0;
        try (FileInputStream fis = new FileInputStream(excellFile);
             Workbook workbook = new XSSFWorkbook(fis);
             ) {
            Connection connection = DriverManager.getConnection(DB_URL);
            stringBuilder.append("INSERT INTO "+TABLE_NAME_PREFIX+generateTableName(excellFile.getName())+
                    " VALUES (?,");
            Sheet sheet = workbook.getSheetAt(0);

            Iterator<Row> rowIterator = sheet.iterator();
            // Procesar encabezados
            Row headerRow = null;
            if (rowIterator.hasNext()) {
                headerRow = rowIterator.next();
            }
            stringBuilder.append(String.join(",", Collections.nCopies(headerRow.getLastCellNum(), "?")) + ")");
            // Procesar datos
            PreparedStatement pstmt = connection.prepareStatement(stringBuilder.toString());

            connection.setAutoCommit(false);
            Calendar cal = Calendar.getInstance();
            Date gl_period = new Date(cal.getTimeInMillis());
            int lastRow = getLastRowWithData((XSSFSheet) sheet);
            int currentRow = 1;
            while (lastRow >= 0) {
                Row row = sheet.getRow(currentRow);
                if (row == null) break;
                for (int i = 0; i < headerRow.getLastCellNum()+1; i++) {
                    columnas++;
                    columnerr++;
                    Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    if (i==0){
                        pstmt.setDate(1, gl_period);
                        columnas++;
                    }
                    if (columnas >  headerRow.getLastCellNum()+1) {
                        columnas =  headerRow.getLastCellNum()+1;
                    }
                    switch (cell.getCellType()) {
                        case STRING:
                            int foundSlash = cell.getStringCellValue().indexOf("/");
                            if (foundSlash == 0 || foundSlash > 0   ) {
                                pstmt.setObject(columnas, null);
                            } else {
                                pstmt.setString(columnas, cell.getStringCellValue());
                            }
                            break;
                        case NUMERIC:
                            if (columnas == ACCOUNT_ID_COLUMN || columnas == ACCOUNT_NUMBER_COLUMN) {
                                pstmt.setString(columnas, BigDecimal.valueOf(cell.getNumericCellValue()).toPlainString().replace(".0",""));
                            } else if (DateUtil.isCellDateFormatted(cell)) {
                                    pstmt.setObject(columnas,  new Date(cell.getDateCellValue().getTime()));
                            } else {
                                if (String.valueOf(cell.getNumericCellValue()).indexOf(".") > 0) {
                                    pstmt.setDouble(columnas,cell.getNumericCellValue());
                                } else {
                                    pstmt.setDouble(columnas,cell.getNumericCellValue());
                                }
                            }
                            break;
                        default:
                            pstmt.setObject(columnas,null);
                            break;

                    }
                } // for
                pstmt.addBatch();
                columnas=0;

                if (++batchCount % 1000 == 0) {
                    System.out.println("Insertando registros");
                    executeBatchWithRetry(pstmt, connection);
                }
                currentRow++;
                rownumber++;
                lastRow--;
            } // while
            executeBatchWithRetry(pstmt, connection);
        }
        catch(SQLException ex) {
            System.out.println("Excepcion de sql en campo "+columnas+" ex["+ex.getMessage()+"]"+"rownumber "+rownumber);
        }
        return batchCount;
    }

    public static int importGLDAYSExcelFile(File excellFile) throws FileNotFoundException,
            SQLException, IOException {
        StringBuilder stringBuilder= new StringBuilder();
        int batchCount = 0;
        int columnas=0;
        int rownumber = 1;
        try (FileInputStream fis = new FileInputStream(excellFile);
             Workbook workbook = new XSSFWorkbook(fis);
             Connection connection = DriverManager.getConnection(DB_URL);
        ) {

            stringBuilder.append("INSERT INTO "+TABLE_NAME_PREFIX+generateTableName(excellFile.getName())+
                    " VALUES (?,");
            Sheet sheet = workbook.getSheetAt(0);
            // Procesar encabezados
            int currentRow = 6;
            Row headerRow = sheet.getRow(currentRow);

            stringBuilder.append(String.join(",", Collections.nCopies(headerRow.getLastCellNum(), "?")) + ")");
            // Procesar datos
            PreparedStatement pstmt = connection.prepareStatement(stringBuilder.toString());
            connection.setAutoCommit(false);
            Calendar cal = Calendar.getInstance();
            Date gl_period = new Date(cal.getTimeInMillis());
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
                for (int i = 0; i < headerRow.getLastCellNum()+1; i++) {
                    columnas++;
                    Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    if (i==0){
                        pstmt.setDate(1, gl_period);
                        columnas++;
                    }
                    if (columnas >  headerRow.getLastCellNum()+1) {
                        columnas =  headerRow.getLastCellNum()+1;
                    }
                    switch (cell.getCellType()) {
                        case STRING:
                            int foundSlash = cell.getStringCellValue().indexOf("/");
                            if (foundSlash == 0 || foundSlash > 0   ) {
                                pstmt.setObject(columnas, null);
                            } else {
                                pstmt.setString(columnas, cell.getStringCellValue());
                            }
                            break;
                        case NUMERIC:
                            if (columnas == ACCOUNT_ID_COLUMN || columnas == ACCOUNT_NUMBER_COLUMN) {
                                pstmt.setString(columnas, BigDecimal.valueOf(cell.getNumericCellValue()).toPlainString().replace(".0",""));
                            } else if (DateUtil.isCellDateFormatted(cell)) {
                                pstmt.setObject(columnas,  new Date(cell.getDateCellValue().getTime()));
                            } else {
                                if (String.valueOf(cell.getNumericCellValue()).indexOf(".") > 0) {
                                    pstmt.setDouble(columnas,cell.getNumericCellValue());
                                } else {
                                    pstmt.setDouble(columnas,cell.getNumericCellValue());
                                }
                            }
                            break;
                        case BLANK:
                            pstmt.setObject(columnas,null);
                            break;
                        default:
                            break;
                    }
                } // for
                pstmt.addBatch();
                columnas=0;
                batchCount++;
                if (batchCount % 100 == 0) {
                    executeBatchWithRetry(pstmt, connection);
                }
                currentRow++;
                rownumber++;
                lastRow--;
            } // while
            executeBatchWithRetry(pstmt, connection);
        }
        catch(SQLException ex) {
            System.out.println("Excepcion de sql en campo "+columnas+" ex["+ex.getMessage()+"]"+"rownumber "+rownumber);
        }
        System.out.println("Total de registros :"+batchCount);
        return batchCount;
    }

    public static class ImportResult {
        public final String tableName;
        public final String[] headers;
        public final List<Object[]> data;
        public final String sourceFile;

        public ImportResult(String tableName, String[] headers, List<Object[]> data, String sourceFile) {
            this.tableName = tableName;
            this.headers = headers;
            this.data = data;
            this.sourceFile = sourceFile;
        }
    }

    private static String generateTableName(String fileName) {
        // Eliminar extensión y caracteres no válidos
        String name = fileName.replaceFirst("[.][^.]+$", "");
        return name.replaceAll("[^a-zA-Z0-9]", "_").toUpperCase();
    }



    private static void executeBatchWithRetry(PreparedStatement pstmt, Connection conn)
            throws SQLException {
        try {
            pstmt.executeBatch();
            pstmt.clearBatch();
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        }
    }

    public static int getLastRowWithData(XSSFSheet sheet) {
        int lastRow = sheet.getLastRowNum();
        while (lastRow >=0 ){
            if (sheet.getRow(lastRow) != null && !isRowEmpty(sheet.getRow(lastRow))) {
                break;
            }
            lastRow--;
        }
        return lastRow;
    }

    private static boolean isRowEmpty(XSSFRow row) {
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
