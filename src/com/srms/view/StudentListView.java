package com.srms.view;

import com.srms.model.Student;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import java.util.List;

/**
 * Student list view with searchable, filterable, sortable table and pagination.
 */
public class StudentListView {
    private final VBox root;
    private final TableView<Student> table;
    private final TextField searchField;
    private final ComboBox<String> courseFilter;
    private final Button addButton, editButton, deleteButton, refreshButton;
    private final Label pageInfoLabel;
    private final Button prevPageBtn, nextPageBtn;
    private final ComboBox<String> sortByCombo;
    private final ToggleButton sortOrderToggle;

    public StudentListView() {
        root = new VBox(15);
        root.setPadding(new Insets(30));
        root.getStyleClass().add("student-list-container");

        // Page title
        Label pageTitle = new Label("\uD83D\uDC65 Student Records");
        pageTitle.getStyleClass().add("page-title");

        // === TOP TOOLBAR ===
        HBox toolbar = new HBox(12);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.getStyleClass().add("toolbar");
        toolbar.setPadding(new Insets(15));

        // Search
        searchField = new TextField();
        searchField.setPromptText("\uD83D\uDD0D Search by name, ID, or email...");
        searchField.getStyleClass().add("search-field");
        searchField.setPrefWidth(280);

        // Course filter
        courseFilter = new ComboBox<>();
        courseFilter.getItems().add("All");
        courseFilter.setValue("All");
        courseFilter.getStyleClass().add("filter-combo");
        courseFilter.setPrefWidth(160);

        // Sort
        sortByCombo = new ComboBox<>();
        sortByCombo.getItems().addAll("ID", "Name", "Age", "Course", "Marks");
        sortByCombo.setValue("ID");
        sortByCombo.getStyleClass().add("filter-combo");
        sortByCombo.setPrefWidth(120);

        sortOrderToggle = new ToggleButton("\u2B06 Asc");
        sortOrderToggle.getStyleClass().add("sort-toggle");
        sortOrderToggle.setOnAction(e -> {
            sortOrderToggle.setText(sortOrderToggle.isSelected() ? "\u2B07 Desc" : "\u2B06 Asc");
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Action buttons
        addButton = new Button("\u2795 Add Student");
        addButton.getStyleClass().add("btn-primary");

        refreshButton = new Button("\uD83D\uDD04");
        refreshButton.getStyleClass().add("btn-icon");
        refreshButton.setTooltip(new Tooltip("Refresh"));

        toolbar.getChildren().addAll(searchField, courseFilter, sortByCombo, sortOrderToggle, spacer, addButton, refreshButton);

        // === TABLE ===
        table = new TableView<>();
        table.getStyleClass().add("student-table");
        table.setPlaceholder(new Label("No students found"));
        VBox.setVgrow(table, Priority.ALWAYS);

        // Columns
        TableColumn<Student, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(60);
        idCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<Student, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(150);

        TableColumn<Student, Integer> ageCol = new TableColumn<>("Age");
        ageCol.setCellValueFactory(new PropertyValueFactory<>("age"));
        ageCol.setPrefWidth(60);
        ageCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<Student, String> courseCol = new TableColumn<>("Course");
        courseCol.setCellValueFactory(new PropertyValueFactory<>("course"));
        courseCol.setPrefWidth(140);

        TableColumn<Student, Double> marksCol = new TableColumn<>("Marks");
        marksCol.setCellValueFactory(new PropertyValueFactory<>("marks"));
        marksCol.setPrefWidth(80);
        marksCol.setStyle("-fx-alignment: CENTER;");

        // Color-coded marks
        marksCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("-fx-alignment: CENTER;");
                } else {
                    setText(String.format("%.1f", item));
                    if (item >= 80) setStyle("-fx-alignment: CENTER; -fx-text-fill: #27ae60; -fx-font-weight: bold;");
                    else if (item >= 50) setStyle("-fx-alignment: CENTER; -fx-text-fill: #f39c12; -fx-font-weight: bold;");
                    else setStyle("-fx-alignment: CENTER; -fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                }
            }
        });

        TableColumn<Student, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setPrefWidth(180);

        TableColumn<Student, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        phoneCol.setPrefWidth(120);

        // Actions column
        TableColumn<Student, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(140);
        actionsCol.setStyle("-fx-alignment: CENTER;");

        table.getColumns().addAll(idCol, nameCol, ageCol, courseCol, marksCol, emailCol, phoneCol, actionsCol);

        // Store reference for external access
        this.actionsColumn = actionsCol;

        // === BOTTOM BAR (Pagination + Info) ===
        HBox bottomBar = new HBox(15);
        bottomBar.setAlignment(Pos.CENTER);
        bottomBar.getStyleClass().add("pagination-bar");
        bottomBar.setPadding(new Insets(10));

        editButton = new Button("\u270F Edit");
        editButton.getStyleClass().add("btn-secondary");
        editButton.setDisable(true);

        deleteButton = new Button("\uD83D\uDDD1 Delete");
        deleteButton.getStyleClass().add("btn-danger");
        deleteButton.setDisable(true);

        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        prevPageBtn = new Button("\u25C0 Prev");
        prevPageBtn.getStyleClass().add("btn-page");

        pageInfoLabel = new Label("Page 1 of 1");
        pageInfoLabel.getStyleClass().add("page-info");

        nextPageBtn = new Button("Next \u25B6");
        nextPageBtn.getStyleClass().add("btn-page");

        bottomBar.getChildren().addAll(editButton, deleteButton, spacer2, prevPageBtn, pageInfoLabel, nextPageBtn);

        // Enable edit/delete when row selected
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            boolean hasSelection = newVal != null;
            editButton.setDisable(!hasSelection);
            deleteButton.setDisable(!hasSelection);
        });

        root.getChildren().addAll(pageTitle, toolbar, table, bottomBar);
    }

    private TableColumn<Student, Void> actionsColumn;

    /**
     * Set action button cell factory (called by controller).
     */
    public void setActionColumnFactory(javafx.util.Callback<TableColumn<Student, Void>, TableCell<Student, Void>> factory) {
        actionsColumn.setCellFactory(factory);
    }

    /**
     * Update the table data.
     */
    public void setStudents(List<Student> students) {
        table.getItems().setAll(students);
    }

    /**
     * Update course filter options.
     */
    public void setCourses(List<String> courses) {
        String current = courseFilter.getValue();
        courseFilter.getItems().clear();
        courseFilter.getItems().add("All");
        courseFilter.getItems().addAll(courses);
        courseFilter.setValue(current != null && courseFilter.getItems().contains(current) ? current : "All");
    }

    /**
     * Update pagination info.
     */
    public void setPageInfo(int currentPage, int totalPages, int totalRecords) {
        pageInfoLabel.setText(String.format("Page %d of %d  (%d records)", currentPage, totalPages, totalRecords));
        prevPageBtn.setDisable(currentPage <= 1);
        nextPageBtn.setDisable(currentPage >= totalPages);
    }

    public String getSortBy() {
        return switch (sortByCombo.getValue()) {
            case "Name" -> "name";
            case "Age" -> "age";
            case "Course" -> "course";
            case "Marks" -> "marks";
            default -> "id";
        };
    }

    public boolean isAscending() { return !sortOrderToggle.isSelected(); }

    // Getters
    public VBox getRoot() { return root; }
    public TableView<Student> getTable() { return table; }
    public TextField getSearchField() { return searchField; }
    public ComboBox<String> getCourseFilter() { return courseFilter; }
    public ComboBox<String> getSortByCombo() { return sortByCombo; }
    public ToggleButton getSortOrderToggle() { return sortOrderToggle; }
    public Button getAddButton() { return addButton; }
    public Button getEditButton() { return editButton; }
    public Button getDeleteButton() { return deleteButton; }
    public Button getRefreshButton() { return refreshButton; }
    public Button getPrevPageBtn() { return prevPageBtn; }
    public Button getNextPageBtn() { return nextPageBtn; }
}
