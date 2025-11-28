package com.via.vix.payroll.util;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import java.nio.file.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.awt.print.PrinterJob;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;

public class PDFUtil {

    public static Path userDocs() {
        String home = System.getProperty("user.home");
        return Paths.get(home, "Documents");
    }

    public static Path savePayslip(Map<String, String> data) throws IOException, DocumentException {
        Path dir = userDocs().resolve("Payslips");
        Files.createDirectories(dir);
        String fileName = "Payslip_" + data.getOrDefault("EMPLOYEE", "employee") + "_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf";
        Path file = dir.resolve(fileName);

        try (FileOutputStream fos = new FileOutputStream(file.toFile())) {
            Document doc = new Document();
            PdfWriter.getInstance(doc, fos);
            doc.open();

            // Header
            Font viaVixFont = new Font(Font.HELVETICA, 36, Font.BOLD);
            Paragraph viaVix = new Paragraph("VIA VIX", viaVixFont);
            viaVix.setAlignment(Element.ALIGN_CENTER);
            doc.add(viaVix);

            Font payslipFont = new Font(Font.HELVETICA, 24, Font.NORMAL);
            Paragraph payslip = new Paragraph("PAYSLIP", payslipFont);
            payslip.setAlignment(Element.ALIGN_CENTER);
            doc.add(payslip);
            doc.add(new Paragraph(" "));

            Font dateFont = new Font(Font.HELVETICA, 12, Font.ITALIC);
            Paragraph date = new Paragraph("DATE: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), dateFont);
            date.setAlignment(Element.ALIGN_CENTER);
            doc.add(date);
            doc.add(new Paragraph(" "));

            // Employee Details Table
            PdfPTable employeeTable = new PdfPTable(2);
            employeeTable.setWidthPercentage(80);
            employeeTable.setSpacingBefore(10f);
            employeeTable.setSpacingAfter(10f);
            employeeTable.getDefaultCell().setBorder(Rectangle.BOX);

            Font labelFont = new Font(Font.HELVETICA, 12, Font.BOLD);
            Font valueFont = new Font(Font.HELVETICA, 12, Font.NORMAL);
            java.awt.Color lightBlue = new java.awt.Color(211, 232, 252);

            String[] employeeLabels = {"EMPLOYEE", "POSITION", "DAYS WORK", "RATE/DAY", "GROSS SALARY"};
            for (String label : employeeLabels) {
                PdfPCell c1 = new PdfPCell(new Phrase(label + ":", labelFont));
                c1.setBackgroundColor(lightBlue);
                c1.setPadding(10);
                employeeTable.addCell(c1);

                PdfPCell c2 = new PdfPCell(new Phrase(data.getOrDefault(label, ""), valueFont));
                c2.setPadding(10);
                employeeTable.addCell(c2);
            }
            doc.add(employeeTable);

            // Deductions Section
            doc.add(new Paragraph("DEDUCTIONS", labelFont));
            doc.add(new Paragraph(" "));

            PdfPTable deductionTable = new PdfPTable(2);
            deductionTable.setWidthPercentage(80);
            deductionTable.setSpacingBefore(10f);
            deductionTable.setSpacingAfter(10f);
            deductionTable.getDefaultCell().setBorder(Rectangle.BOX);

            String[] deductionLabels = {"SSS", "PAG-IBIG", "PHILHEALTH", "TOTAL DEDUCTIONS", "NET SALARY"};
            for (String label : deductionLabels) {
                PdfPCell c1 = new PdfPCell(new Phrase(label + ":", labelFont));
                c1.setBackgroundColor(lightBlue);
                c1.setPadding(10);
                deductionTable.addCell(c1);

                PdfPCell c2 = new PdfPCell(new Phrase(data.getOrDefault(label, ""), valueFont));
                c2.setPadding(10);
                deductionTable.addCell(c2);
            }
            doc.add(deductionTable);
            doc.add(new Paragraph(" "));
            doc.add(new Paragraph(" "));

            // Footer (Added Logo)
            try {
                // Load the image directly from the absolute file path
                Image logo = Image.getInstance(UIConfig.LOGO_PATH);
                logo.setAlignment(Element.ALIGN_CENTER);
                logo.scaleToFit(UIConfig.PDF_PAYSLIP_LOGO_SIZE, UIConfig.PDF_PAYSLIP_LOGO_SIZE);
                doc.add(logo);
            } catch (Exception e) {
                System.err.println("Error loading image for payslip: " + e.getMessage());
                Paragraph logoText = new Paragraph("VIA VIX Logo (Error loading image)", new Font(Font.HELVETICA, 10, Font.ITALIC));
                logoText.setAlignment(Element.ALIGN_CENTER);
                doc.add(logoText);
            }

            Font footerViaVixFont = new Font(Font.HELVETICA, 18, Font.BOLD);
            Paragraph footerViaVix = new Paragraph("VIA VIX", footerViaVixFont);
            footerViaVix.setAlignment(Element.ALIGN_CENTER);
            doc.add(footerViaVix);

            Font footerFont = new Font(Font.HELVETICA, 10, Font.NORMAL);
            Paragraph contact = new Paragraph("contact: 09672145944\nSan pascual Talavera, Nueva Ecija, zone 5, bldg234", footerFont);
            contact.setAlignment(Element.ALIGN_CENTER);
            doc.add(contact);

            doc.add(new Paragraph(" "));

            Paragraph assistance = new Paragraph("If you need further assistance, please feel free to", footerFont);
            assistance.setAlignment(Element.ALIGN_CENTER);
            doc.add(assistance);

            Font emailFont = new Font(Font.HELVETICA, 10, Font.UNDERLINE);
            Paragraph email = new Paragraph("contact Admin at viavix@email.com", emailFont);
            email.setAlignment(Element.ALIGN_CENTER);
            doc.add(email);

            doc.close();
        }
        return file;
    }

    public static void printPayslip(Map<String, String> data) throws Exception {
        Path file = savePayslip(data);
        try (PDDocument document = Loader.loadPDF(file.toFile())) {
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPageable(new PDFPageable(document));
            if (job.printDialog()) {
                job.print();
            }
        }
    }

    public static Path saveReport(String title, String content, List<Object[]> payrollData) throws IOException, DocumentException {
        Path dir = userDocs().resolve("Reports");
        Files.createDirectories(dir);
        String fileName = title.replaceAll("[^a-zA-Z0-9_-]", "_") + "_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf";
        Path file = dir.resolve(fileName);
        
        try (FileOutputStream fos = new FileOutputStream(file.toFile())) {
            Document doc = new Document();
            PdfWriter.getInstance(doc, fos);
            doc.open();

            // Header (Styled like the payslip)
            Font viaVixFont = new Font(Font.HELVETICA, 36, Font.BOLD);
            Paragraph viaVix = new Paragraph("VIA VIX", viaVixFont);
            viaVix.setAlignment(Element.ALIGN_CENTER);
            doc.add(viaVix);

            Font reportTitleFont = new Font(Font.HELVETICA, 24, Font.NORMAL);
            Paragraph reportTitle = new Paragraph(title.toUpperCase(), reportTitleFont);
            reportTitle.setAlignment(Element.ALIGN_CENTER);
            doc.add(reportTitle);
            doc.add(new Paragraph(" "));

            Font dateFont = new Font(Font.HELVETICA, 12, Font.ITALIC);
            Paragraph date = new Paragraph("DATE: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), dateFont);
            date.setAlignment(Element.ALIGN_CENTER);
            doc.add(date);
            doc.add(new Paragraph(" "));
            doc.add(new Paragraph(" "));

            // Content Section
            Paragraph notesParagraph = new Paragraph(content);
            notesParagraph.setAlignment(Element.ALIGN_JUSTIFIED);
            doc.add(notesParagraph);
            doc.add(new Paragraph(" "));

            // Payroll History Table (if data is provided)
            if (payrollData != null && !payrollData.isEmpty()) {
                doc.add(new Paragraph(" "));
                Font tableHeaderFont = new Font(Font.HELVETICA, 14, Font.BOLD);
                Paragraph payrollTitle = new Paragraph("Payroll History", tableHeaderFont);
                payrollTitle.setSpacingAfter(10f);
                doc.add(payrollTitle);

                String[] columnNames = {"ID", "Emp. ID", "Name", "Position", "Days", "Rate", "Salary", "Date"};
                PdfPTable payrollTable = new PdfPTable(columnNames.length);
                payrollTable.setWidthPercentage(100);

                // Add table headers
                Font headerFont = new Font(Font.HELVETICA, 10, Font.BOLD);
                for (String col : columnNames) {
                    PdfPCell headerCell = new PdfPCell(new Phrase(col, headerFont));
                    headerCell.setBackgroundColor(new java.awt.Color(211, 232, 252));
                    headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    headerCell.setPadding(5);
                    payrollTable.addCell(headerCell);
                }

                // Add table data
                Font dataFont = new Font(Font.HELVETICA, 9, Font.NORMAL);
                for (Object[] row : payrollData) {
                    for (Object cellData : row) {
                        PdfPCell dataCell = new PdfPCell(new Phrase(String.valueOf(cellData), dataFont));
                        dataCell.setPadding(4);
                        payrollTable.addCell(dataCell);
                    }
                }
                doc.add(payrollTable);
            }



            // Spacer to push footer down
            doc.add(new Paragraph(" "));
            doc.add(new Paragraph(" "));

            // Footer (Added Logo)
            try {
                // Load the image directly from the absolute file path
                Image logo = Image.getInstance(UIConfig.LOGO_PATH);
                logo.setAlignment(Element.ALIGN_CENTER);
                logo.scaleToFit(UIConfig.PDF_REPORT_LOGO_SIZE, UIConfig.PDF_REPORT_LOGO_SIZE);
                doc.add(logo);
            } catch (Exception e) {
                System.err.println("Error loading image for report: " + e.getMessage());
                Paragraph logoText = new Paragraph("VIA VIX Logo (Error loading image)", new Font(Font.HELVETICA, 10, Font.ITALIC));
                doc.add(logoText);
            }

            Font footerViaVixFont = new Font(Font.HELVETICA, 18, Font.BOLD);
            Paragraph footerViaVix = new Paragraph("VIA VIX", footerViaVixFont);
            footerViaVix.setAlignment(Element.ALIGN_CENTER);
            doc.add(footerViaVix);

            Font footerFont = new Font(Font.HELVETICA, 10, Font.NORMAL);
            Paragraph contact = new Paragraph("contact: 09672145944\nSan pascual Talavera, Nueva Ecija, zone 5, bldg234", footerFont);
            contact.setAlignment(Element.ALIGN_CENTER);
            doc.add(contact);

            doc.close();
        }
        return file;
    }
}