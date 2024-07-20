package com.example.exportPDForExcel.PDF;

import java.awt.Color;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import jakarta.servlet.http.HttpServletResponse;


public class UserPDFExporter<T> {
    private final List<T> listUsers;
    private final List<Field> fields;

    public UserPDFExporter(Class<T> tClass, List<T> listUsers) {
        this.listUsers = listUsers;
        Field[] fieldsArray = tClass.getDeclaredFields();
        fields = new ArrayList<>(Arrays.asList(fieldsArray));
    }

    public UserPDFExporter(Class<T> tClass, List<T> listUsers, List<String> fieldNames) {
        this.listUsers = listUsers;
        Field[] fieldsArray = tClass.getDeclaredFields();
        List<Field> list = new ArrayList<>();
        for (String s : fieldNames) {
            for (Field field : fieldsArray) {
                if (field.getName().equalsIgnoreCase(s)) {
                    list.add(field);
                }
            }
        }
        fields = list;
    }

    private void writeTableHeader(PdfPTable table) {
//        Field[] fields = tClass.getDeclaredFields();
        for (Field field : fields) {
            PdfPCell header = new PdfPCell();
            header.setPhrase(new Phrase(field.getName()));
            header.setBackgroundColor(Color.gray);
            header.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(header);
        }
    }

    private void writeTableData(PdfPTable table) {
        for (T user : listUsers) {
//            Field[] fields = tClass.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                try {
                    Object value = field.get(user);
                    table.addCell(value != null ? value.toString() : "");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void export(HttpServletResponse response) throws DocumentException, IOException {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();
//        PdfPTable table = new PdfPTable(tClass.getDeclaredFields().length);
        PdfPTable table = new PdfPTable(fields.size());
        table.setWidthPercentage(100);
        writeTableHeader(table);
        writeTableData(table);

        document.add(table);
        document.close();
    }
}