package com.srms;

import com.srms.config.DatabaseConfig;
import com.srms.controller.*;
import com.srms.dao.ActivityLogDAO;
import com.srms.util.*;
import com.srms.view.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;

/**
 * Main application entry point.
 * Bootstraps database, shows login, and manages navigation.
 */
public class App extends Application {
    private Stage primaryStage;
    private Scene scene;
    private LoginController loginController;
    private MainLayout mainLayout;
    private DashboardController dashboardController;
    private StudentController studentController;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        stage.setTitle("SRMS — Student Record Management System");
        stage.setMinWidth(1100);
        stage.setMinHeight(700);

        // Initialize database
        try {
            DatabaseConfig.runMigrations();
        } catch (Exception e) {
            System.err.println("[App] Database initialization failed: " + e.getMessage());
            e.printStackTrace();
        }

        // Show login screen
        showLogin();

        stage.setWidth(1200);
        stage.setHeight(800);
        stage.show();
    }

    private void showLogin() {
        loginController = new LoginController();
        loginController.setOnLoginSuccess(this::onLoginSuccess);

        StackPane loginRoot = loginController.getView().getRoot();
        scene = new Scene(loginRoot);
        ThemeManager.applyTheme(scene);
        primaryStage.setScene(scene);
    }

    private void onLoginSuccess() {
        String username = loginController.getLoggedInUser().getUsername();

        // Create main layout
        mainLayout = new MainLayout(username);

        // Create controllers
        dashboardController = new DashboardController();
        studentController = new StudentController();
        studentController.setNotificationRoot(mainLayout.getRoot());

        // Create export/import view
        ExportImportView exportView = new ExportImportView();

        // Wire export buttons
        exportView.getExportCSVBtn().setOnAction(e -> studentController.handleExportCSV());
        exportView.getExportPDFBtn().setOnAction(e -> studentController.handleExportPDF());
        exportView.getImportCSVBtn().setOnAction(e -> {
            studentController.handleImportCSV();
            dashboardController.refresh();
        });
        exportView.getBackupBtn().setOnAction(e -> handleBackup());
        exportView.getRestoreBtn().setOnAction(e -> handleRestore());

        // Wire sidebar navigation
        mainLayout.setOnDashboard(() -> {
            dashboardController.refresh();
            mainLayout.setContent(dashboardController.getView().getRoot());
        });
        mainLayout.setOnStudents(() -> {
            studentController.refresh();
            mainLayout.setContent(studentController.getView().getRoot());
        });
        mainLayout.setOnExport(() -> {
            mainLayout.setContent(exportView.getRoot());
        });
        mainLayout.setOnLogout(() -> {
            new ActivityLogDAO().logActivity("LOGOUT", "User logged out.");
            showLogin();
        });

        // Set scene to main layout
        scene = new Scene(mainLayout.getRoot());
        ThemeManager.applyTheme(scene);
        primaryStage.setScene(scene);

        // Show dashboard by default
        dashboardController.refresh();
        mainLayout.setContent(dashboardController.getView().getRoot());

        NotificationManager.showSuccess(mainLayout.getRoot(), "Welcome back, " + username + "!");
    }

    private void handleBackup() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save Database Backup");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("SQL Files", "*.sql"));
        chooser.setInitialFileName("student_db_backup.sql");
        File file = chooser.showSaveDialog(primaryStage);
        if (file != null) {
            try {
                String mysqldump = "C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\mysqldump.exe";
                ProcessBuilder pb = new ProcessBuilder(mysqldump, "-u", "root", "-pPassword", "student_db",
                        "--result-file=" + file.getAbsolutePath());
                pb.redirectErrorStream(true);
                Process p = pb.start();
                int exitCode = p.waitFor();
                if (exitCode == 0) {
                    new ActivityLogDAO().logActivity("BACKUP", "Database backed up to: " + file.getName());
                    NotificationManager.showSuccess(mainLayout.getRoot(), "Database backup created successfully!");
                } else {
                    NotificationManager.showError(mainLayout.getRoot(), "Backup failed (exit code: " + exitCode + ")");
                }
            } catch (Exception e) {
                NotificationManager.showError(mainLayout.getRoot(), "Backup error: " + e.getMessage());
            }
        }
    }

    private void handleRestore() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select Backup File to Restore");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("SQL Files", "*.sql"));
        File file = chooser.showOpenDialog(primaryStage);
        if (file != null) {
            javafx.scene.control.Alert confirm = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
            confirm.setTitle("Confirm Restore");
            confirm.setHeaderText("Restore Database");
            confirm.setContentText("This will OVERWRITE your current data with the backup. Continue?");
            confirm.showAndWait().ifPresent(btn -> {
                if (btn == javafx.scene.control.ButtonType.OK) {
                    try {
                        String mysql = "C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\mysql.exe";
                        ProcessBuilder pb = new ProcessBuilder(mysql, "-u", "root", "-pPassword", "student_db",
                                "-e", "source " + file.getAbsolutePath());
                        pb.redirectErrorStream(true);
                        Process p = pb.start();
                        int exitCode = p.waitFor();
                        if (exitCode == 0) {
                            new ActivityLogDAO().logActivity("RESTORE", "Database restored from: " + file.getName());
                            NotificationManager.showSuccess(mainLayout.getRoot(), "Database restored successfully!");
                            dashboardController.refresh();
                        } else {
                            NotificationManager.showError(mainLayout.getRoot(), "Restore failed.");
                        }
                    } catch (Exception e) {
                        NotificationManager.showError(mainLayout.getRoot(), "Restore error: " + e.getMessage());
                    }
                }
            });
        }
    }

    @Override
    public void stop() {
        DatabaseConfig.closeConnection();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
