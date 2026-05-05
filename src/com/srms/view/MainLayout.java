package com.srms.view;

import com.srms.util.ThemeManager;
import javafx.animation.*;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

/**
 * Main application layout with animated sidebar navigation and content area.
 */
public class MainLayout {
    private final StackPane root;
    private final BorderPane mainPane;
    private final StackPane contentArea;
    private final VBox sidebar;
    private final Label welcomeLabel;
    private final Button themeToggle;

    private Button activeButton;

    // Navigation callbacks
    private Runnable onDashboard, onStudents, onExport, onLogout;

    public MainLayout(String username) {
        root = new StackPane();
        mainPane = new BorderPane();
        root.getChildren().add(mainPane);

        // === SIDEBAR ===
        sidebar = new VBox(8);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(250);
        sidebar.setPadding(new Insets(20, 15, 20, 15));

        // Brand header
        VBox brandBox = new VBox(4);
        brandBox.setAlignment(Pos.CENTER);
        brandBox.setPadding(new Insets(10, 0, 30, 0));

        Label brandIcon = new Label("\uD83C\uDF93");
        brandIcon.setStyle("-fx-font-size: 40px;");
        Label brandName = new Label("SRMS");
        brandName.getStyleClass().add("sidebar-brand");
        Label brandSub = new Label("Management System");
        brandSub.getStyleClass().add("sidebar-brand-sub");
        brandBox.getChildren().addAll(brandIcon, brandName, brandSub);

        // Navigation buttons
        Button dashBtn = createNavButton("\uD83D\uDCCA", "Dashboard");
        Button studentsBtn = createNavButton("\uD83D\uDC65", "Students");
        Button exportBtn = createNavButton("\uD83D\uDCE5", "Export / Import");

        // Spacer
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Theme toggle
        themeToggle = new Button("\uD83C\uDF19 Dark Mode");
        themeToggle.getStyleClass().addAll("nav-button", "theme-toggle");
        themeToggle.setMaxWidth(Double.MAX_VALUE);
        themeToggle.setOnAction(e -> {
            ThemeManager.toggleTheme(root.getScene());
            themeToggle.setText(ThemeManager.isDarkMode() ? "\u2600 Light Mode" : "\uD83C\uDF19 Dark Mode");
        });

        // User info + Logout
        VBox userBox = new VBox(8);
        userBox.getStyleClass().add("user-box");
        userBox.setPadding(new Insets(15, 10, 10, 10));

        welcomeLabel = new Label("\uD83D\uDC64 " + (username != null ? username : "Admin"));
        welcomeLabel.getStyleClass().add("user-label");

        Button logoutBtn = new Button("\uD83D\uDEAA Logout");
        logoutBtn.getStyleClass().addAll("nav-button", "logout-button");
        logoutBtn.setMaxWidth(Double.MAX_VALUE);
        logoutBtn.setOnAction(e -> { if (onLogout != null) onLogout.run(); });

        userBox.getChildren().addAll(welcomeLabel, logoutBtn);

        sidebar.getChildren().addAll(brandBox, dashBtn, studentsBtn, exportBtn, spacer, themeToggle, userBox);

        // Wire navigation
        dashBtn.setOnAction(e -> { setActive(dashBtn); if (onDashboard != null) onDashboard.run(); });
        studentsBtn.setOnAction(e -> { setActive(studentsBtn); if (onStudents != null) onStudents.run(); });
        exportBtn.setOnAction(e -> { setActive(exportBtn); if (onExport != null) onExport.run(); });

        // Default active
        setActive(dashBtn);

        // === CONTENT AREA ===
        contentArea = new StackPane();
        contentArea.getStyleClass().add("content-area");
        contentArea.setPadding(new Insets(0));

        mainPane.setLeft(sidebar);
        mainPane.setCenter(contentArea);
    }

    private Button createNavButton(String icon, String text) {
        Button btn = new Button(icon + "  " + text);
        btn.getStyleClass().add("nav-button");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        return btn;
    }

    private void setActive(Button btn) {
        if (activeButton != null) activeButton.getStyleClass().remove("nav-active");
        btn.getStyleClass().add("nav-active");
        activeButton = btn;
    }

    /**
     * Set the content area with a fade transition.
     */
    public void setContent(Node content) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(150), contentArea);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> {
            contentArea.getChildren().setAll(content);
            FadeTransition fadeIn = new FadeTransition(Duration.millis(250), contentArea);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();
        });
        fadeOut.play();
    }

    // Setters for navigation callbacks
    public void setOnDashboard(Runnable r) { this.onDashboard = r; }
    public void setOnStudents(Runnable r) { this.onStudents = r; }
    public void setOnExport(Runnable r) { this.onExport = r; }
    public void setOnLogout(Runnable r) { this.onLogout = r; }

    public StackPane getRoot() { return root; }
    public BorderPane getMainPane() { return mainPane; }
    public StackPane getContentArea() { return contentArea; }
}
