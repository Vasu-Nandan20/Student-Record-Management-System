package com.srms.view;

import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

/**
 * Export/Import view with styled action cards.
 */
public class ExportImportView {
    private final VBox root;
    private final Button exportCSVBtn, exportPDFBtn, importCSVBtn, backupBtn, restoreBtn;

    public ExportImportView() {
        root = new VBox(25);
        root.setPadding(new Insets(30));
        root.getStyleClass().add("export-container");

        Label pageTitle = new Label("\uD83D\uDCE5 Export & Import");
        pageTitle.getStyleClass().add("page-title");

        // Export section
        Label exportTitle = new Label("Export Data");
        exportTitle.getStyleClass().add("section-title");

        HBox exportCards = new HBox(20);
        exportCards.setAlignment(Pos.CENTER_LEFT);

        exportCSVBtn = createActionCard("\uD83D\uDCC4", "Export to CSV",
            "Export all student records to a CSV spreadsheet file.", "action-card-blue");
        exportPDFBtn = createActionCard("\uD83D\uDCD5", "Export to PDF",
            "Generate a professional PDF report of all students.", "action-card-red");

        exportCards.getChildren().addAll(exportCSVBtn, exportPDFBtn);

        // Import section
        Label importTitle = new Label("Import Data");
        importTitle.getStyleClass().add("section-title");

        HBox importCards = new HBox(20);
        importCards.setAlignment(Pos.CENTER_LEFT);

        importCSVBtn = createActionCard("\uD83D\uDCE5", "Import from CSV",
            "Import student records from a CSV file into the database.", "action-card-green");

        importCards.getChildren().add(importCSVBtn);

        // Backup section
        Label backupTitle = new Label("Database Backup");
        backupTitle.getStyleClass().add("section-title");

        HBox backupCards = new HBox(20);
        backupCards.setAlignment(Pos.CENTER_LEFT);

        backupBtn = createActionCard("\uD83D\uDCBE", "Backup Database",
            "Create a full backup of the student_db database.", "action-card-purple");
        restoreBtn = createActionCard("\uD83D\uDD04", "Restore Database",
            "Restore database from a previous backup file.", "action-card-orange");

        backupCards.getChildren().addAll(backupBtn, restoreBtn);

        root.getChildren().addAll(pageTitle, exportTitle, exportCards, importTitle, importCards, backupTitle, backupCards);
    }

    private Button createActionCard(String icon, String title, String description, String styleClass) {
        VBox content = new VBox(8);
        content.setAlignment(Pos.CENTER_LEFT);

        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 32px;");
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("action-card-title");
        Label descLabel = new Label(description);
        descLabel.getStyleClass().add("action-card-desc");
        descLabel.setWrapText(true);

        content.getChildren().addAll(iconLabel, titleLabel, descLabel);

        Button card = new Button();
        card.setGraphic(content);
        card.getStyleClass().addAll("action-card", styleClass);
        card.setPrefSize(280, 150);
        card.setMaxWidth(280);
        return card;
    }

    public Button getExportCSVBtn() { return exportCSVBtn; }
    public Button getExportPDFBtn() { return exportPDFBtn; }
    public Button getImportCSVBtn() { return importCSVBtn; }
    public Button getBackupBtn() { return backupBtn; }
    public Button getRestoreBtn() { return restoreBtn; }
    public VBox getRoot() { return root; }
}
