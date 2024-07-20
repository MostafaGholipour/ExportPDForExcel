package com.example.exportPDForExcel.controller;

import com.example.exportPDForExcel.PDF.UserPDFExporter;
import com.example.exportPDForExcel.model.User;
import com.example.exportPDForExcel.Excel.UserExcelExporter;
import com.example.exportPDForExcel.service.UserServices;
import com.lowagie.text.DocumentException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class UserController {

    @Autowired
    private UserServices service;


    @GetMapping("/users/export/excel")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=users_" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);

        List<User> listUsers = service.listAll();

        UserExcelExporter excelExporter = new UserExcelExporter(User.class, listUsers);

        excelExporter.export(response);
    }

    @GetMapping("/users/export/pdf")
    public void exportToPDF(HttpServletResponse response) throws DocumentException, IOException {
        response.setContentType("application/pdf");
        DateFormat dateFormatter = new SimpleDateFormat("+yyyy.MM.dd - HH.mm.ss+");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=users_" + currentDateTime + ".pdf";
        response.setHeader(headerKey, headerValue);

        List<User> listUsers = service.listAll();


        List<String> fields = new ArrayList<>();
        fields.add("iD");
        fields.add("email");
        fields.add("fullName");

        UserPDFExporter exporter = new UserPDFExporter(User.class, listUsers,fields);
        exporter.export(response);

    }

}