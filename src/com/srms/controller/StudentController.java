package com.srms.controller;

import com.srms.dao.ActivityLogDAO;
import com.srms.model.Student;
import com.srms.service.ExportService;
import com.srms.service.StudentService;
import com.srms.util.NotificationManager;
import com.srms.view.StudentFormDialog;
import com.srms.view.StudentListView;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import java.io.File;
import java.util.*;

/**
 * Controller for the student list view.
 * Handles CRUD operations, search, filter, sort, and pagination.
 */
public class StudentController {
    private final StudentListView view;
    private final StudentService studentService;
    private final ExportService exportService;
    private final ActivityLogDAO logDAO;
    private StackPane notificationRoot;

    private int currentPage = 1;
    private final int pageSize = 10;

    public StudentController() {
        this.view = new StudentListView();
        this.studentService = new StudentService();
        this.exportService = new ExportService();
        this.logDAO = new ActivityLogDAO();
        setupEventHandlers();
    }

    public void setNotificationRoot(StackPane root) {
        this.notificationRoot = root;
    }

    private void setupEventHandlers() {
        // Add student
        view.getAddButton().setOnAction(e -> handleAdd());

        // Edit student
        view.getEditButton().setOnAction(e -> handleEdit());

        // Delete student
        view.getDeleteButton().setOnAction(e -> handleDelete());

        // Refresh
        view.getRefreshButton().setOnAction(e -> refresh());

        // Search (with slight delay)
        view.getSearchField().textProperty().addListener((o, ov, nv) -> {
            currentPage = 1;
            refresh();
        });

        // Course filter
        view.getCourseFilter().setOnAction(e -> {
            currentPage = 1;
            refresh();
        });

        // Sort
        view.getSortByCombo().setOnAction(e -> refresh());
        view.getSortOrderToggle().setOnAction(e -> refresh());

        // Pagination
        view.getPrevPageBtn().setOnAction(e -> { currentPage--; refresh(); });
        view.getNextPageBtn().setOnAction(e -> { currentPage++; refresh(); });

        // Double-click to edit
        view.getTable().setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) handleEdit();
        });
    }

    /**
     * Refresh the table data with current filters and pagination.
     */
    public void refresh() {
        try {
            String search = view.getSearchField().getText();
            String course = view.getCourseFilter().getValue();
            String sortBy = view.getSortBy();
            boolean ascending = view.isAscending();

            List<Student> students = studentService.getStudentsPaginated(
                search, course, 0, 0, sortBy, ascending, currentPage, pageSize
            );
            int totalPages = studentService.getTotalPages(search, course, 0, 0, pageSize);
            int totalCount = studentService.getTotalCount(search, course, 0, 0);

            view.setStudents(students);
            view.setPageInfo(currentPage, totalPages, totalCount);

            // Update course filter options
            List<String> courses = studentService.getAllCourses();
            view.setCourses(courses);

        } catch (Exception e) {
            showError("Failed to load students: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleAdd() {
        Optional<Student> result = StudentFormDialog.showAddDialog();
        result.ifPresent(student -> {
            try {
                if (studentService.addStudent(student)) {
                    showSuccess("Student '" + student.getName() + "' added successfully!");
                    refresh();
                } else {
                    showError("Failed to add student.");
                }
            } catch (Exception e) {
                showError("Error: " + e.getMessage());
            }
        });
    }

    private void handleEdit() {
        Student selected = view.getTable().getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Please select a student to edit.");
            return;
        }

        Optional<Student> result = StudentFormDialog.showEditDialog(selected);
        result.ifPresent(student -> {
            try {
                if (studentService.updateStudent(student)) {
                    showSuccess("Student '" + student.getName() + "' updated successfully!");
                    refresh();
                } else {
                    showError("Failed to update student.");
                }
            } catch (Exception e) {
                showError("Error: " + e.getMessage());
            }
        });
    }

    private void handleDelete() {
        Student selected = view.getTable().getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Please select a student to delete.");
            return;
        }

        // Confirmation dialog
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Student Record");
        confirm.setContentText("Are you sure you want to delete '" + selected.getName() + "' (ID: " + selected.getId() + ")?\nThis action cannot be undone.");
        confirm.getDialogPane().getStyleClass().add("confirm-dialog");

        Optional<ButtonType> choice = confirm.showAndWait();
        if (choice.isPresent() && choice.get() == ButtonType.OK) {
            try {
                if (studentService.deleteStudent(selected.getId())) {
                    showSuccess("Student '" + selected.getName() + "' deleted.");
                    refresh();
                } else {
                    showError("Failed to delete student.");
                }
            } catch (Exception e) {
                showError("Error: " + e.getMessage());
            }
        }
    }

    /**
     * Handle export to CSV.
     */
    public void handleExportCSV() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Export to CSV");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        chooser.setInitialFileName("students_export.csv");
        File file = chooser.showSaveDialog(null);
        if (file != null) {
            try {
                List<Student> students = studentService.getAllStudents();
                exportService.exportToCSV(students, file.getAbsolutePath());
                logDAO.logActivity("EXPORT", "Exported " + students.size() + " students to CSV");
                showSuccess("Exported " + students.size() + " records to CSV!");
            } catch (Exception e) {
                showError("Export failed: " + e.getMessage());
            }
        }
    }

    /**
     * Handle export to PDF.
     */
    public void handleExportPDF() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Export to PDF");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        chooser.setInitialFileName("students_export.pdf");
        File file = chooser.showSaveDialog(null);
        if (file != null) {
            try {
                List<Student> students = studentService.getAllStudents();
                exportService.exportToPDF(students, file.getAbsolutePath());
                logDAO.logActivity("EXPORT", "Exported " + students.size() + " students to PDF");
                showSuccess("Exported " + students.size() + " records to PDF!");
            } catch (Exception e) {
                showError("PDF export failed: " + e.getMessage());
            }
        }
    }

    /**
     * Handle import from CSV.
     */
    public void handleImportCSV() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Import from CSV");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = chooser.showOpenDialog(null);
        if (file != null) {
            try {
                List<Student> students = exportService.importFromCSV(file.getAbsolutePath());
                int count = 0;
                for (Student s : students) {
                    if (studentService.addStudent(s)) count++;
                }
                logDAO.logActivity("IMPORT", "Imported " + count + " students from CSV");
                showSuccess("Imported " + count + " students from CSV!");
                refresh();
            } catch (Exception e) {
                showError("Import failed: " + e.getMessage());
            }
        }
    }

    // Notification helpers
    private void showSuccess(String msg) {
        if (notificationRoot != null) NotificationManager.showSuccess(notificationRoot, msg);
    }
    private void showError(String msg) {
        if (notificationRoot != null) NotificationManager.showError(notificationRoot, msg);
    }
    private void showWarning(String msg) {
        if (notificationRoot != null) NotificationManager.showWarning(notificationRoot, msg);
    }

    public StudentListView getView() { return view; }
}
