package com.srms.service;

import com.srms.model.Student;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * Service for exporting students to CSV/PDF and importing from CSV.
 */
public class ExportService {

    /**
     * Export students to CSV file.
     */
    public void exportToCSV(List<Student> students, String filePath) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("ID,Name,Age,Course,Marks,Email,Phone");
            for (Student s : students) {
                writer.printf("%d,\"%s\",%d,\"%s\",%.2f,\"%s\",\"%s\"%n",
                    s.getId(),
                    escapeCsv(s.getName()),
                    s.getAge(),
                    escapeCsv(s.getCourse()),
                    s.getMarks(),
                    escapeCsv(s.getEmail() != null ? s.getEmail() : ""),
                    escapeCsv(s.getPhone() != null ? s.getPhone() : "")
                );
            }
        }
    }

    /**
     * Import students from CSV file.
     * Returns list of parsed students (without IDs — for insertion).
     */
    public List<Student> importFromCSV(String filePath) throws IOException {
        List<Student> students = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String header = reader.readLine(); // Skip header
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = parseCSVLine(line);
                if (parts.length >= 5) {
                    Student s = new Student();
                    // Skip ID (parts[0]) — auto-generated
                    s.setName(parts.length > 1 ? parts[1].trim() : "");
                    s.setAge(parts.length > 2 ? Integer.parseInt(parts[2].trim()) : 0);
                    s.setCourse(parts.length > 3 ? parts[3].trim() : "");
                    s.setMarks(parts.length > 4 ? Double.parseDouble(parts[4].trim()) : 0);
                    s.setEmail(parts.length > 5 ? parts[5].trim() : "");
                    s.setPhone(parts.length > 6 ? parts[6].trim() : "");
                    students.add(s);
                }
            }
        }
        return students;
    }

    /**
     * Export students to PDF file using iTextPDF.
     */
    public void exportToPDF(List<Student> students, String filePath) throws Exception {
        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();

        // Title
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD, new BaseColor(102, 126, 234));
        Paragraph title = new Paragraph("Student Record Management System", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(10);
        document.add(title);

        Font subtitleFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.GRAY);
        Paragraph subtitle = new Paragraph("Generated on: " + new java.util.Date(), subtitleFont);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        subtitle.setSpacingAfter(20);
        document.add(subtitle);

        // Table
        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1, 3, 1.2f, 2.5f, 1.5f, 3, 2});

        // Header
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.WHITE);
        BaseColor headerBg = new BaseColor(102, 126, 234);
        String[] headers = {"ID", "Name", "Age", "Course", "Marks", "Email", "Phone"};
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
            cell.setBackgroundColor(headerBg);
            cell.setPadding(8);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }

        // Rows
        Font cellFont = new Font(Font.FontFamily.HELVETICA, 9);
        BaseColor altRow = new BaseColor(245, 247, 255);
        boolean alt = false;
        for (Student s : students) {
            BaseColor bg = alt ? altRow : BaseColor.WHITE;
            addCell(table, String.valueOf(s.getId()), cellFont, bg);
            addCell(table, s.getName(), cellFont, bg);
            addCell(table, String.valueOf(s.getAge()), cellFont, bg);
            addCell(table, s.getCourse(), cellFont, bg);
            addCell(table, String.format("%.1f", s.getMarks()), cellFont, bg);
            addCell(table, s.getEmail() != null ? s.getEmail() : "-", cellFont, bg);
            addCell(table, s.getPhone() != null ? s.getPhone() : "-", cellFont, bg);
            alt = !alt;
        }

        document.add(table);

        // Footer
        Paragraph footer = new Paragraph("\nTotal Students: " + students.size(), subtitleFont);
        footer.setAlignment(Element.ALIGN_RIGHT);
        document.add(footer);

        document.close();
    }

    private void addCell(PdfPTable table, String text, Font font, BaseColor bg) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(bg);
        cell.setPadding(6);
        table.addCell(cell);
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        return value.replace("\"", "\"\"");
    }

    private String[] parseCSVLine(String line) {
        List<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder current = new StringBuilder();
        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(current.toString());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        result.add(current.toString());
        return result.toArray(new String[0]);
    }
}
