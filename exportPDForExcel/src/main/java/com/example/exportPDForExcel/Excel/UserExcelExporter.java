package com.example.exportPDForExcel.Excel;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.Set;
import java.util.stream.Collectors;

public class UserExcelExporter<T> {
    private Class<T> tClass;
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<T> listUsers;

    public UserExcelExporter(Class<T> tClass, List<T> listUsers) {
        this.tClass = tClass;
        this.listUsers = listUsers;
        workbook = new XSSFWorkbook();
    }

    private void writeHeaderLine() {
        sheet = workbook.createSheet(tClass.getSimpleName());

        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.PINK.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Field[] fields = tClass.getDeclaredFields();
        int i = 0;
        for (Field field : fields) {
            createCell(row, i++, field.getName(), style);
        }
    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value instanceof Set) {
            String setAsString = ((Set<?>) value).stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));
            cell.setCellValue(setAsString);
        } else {
            cell.setCellValue(value != null ? value.toString() : "");
        }
        cell.setCellStyle(style);
    }

    private void writeDataLines() {
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);

        for (T user : listUsers) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            Field[] fields = tClass.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                try {
                    Object value = field.get(user);
                    createCell(row, columnCount++, value, style);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void export(HttpServletResponse response) throws IOException {
        writeHeaderLine();
        writeDataLines();

        try (ServletOutputStream outputStream = response.getOutputStream()) {
            workbook.write(outputStream);
        }
        workbook.close();
    }
}
