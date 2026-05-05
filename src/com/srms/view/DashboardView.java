package com.srms.view;

import com.srms.model.ActivityLog;
import com.srms.model.Student;
import javafx.geometry.*;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Dashboard view with stat cards, charts, and recent activity feed.
 */
public class DashboardView {
    private final ScrollPane root;
    private final VBox container;

    // Stat card labels
    private final Label totalStudentsValue = new Label("0");
    private final Label avgMarksValue = new Label("0.0");
    private final Label topPerformerValue = new Label("-");
    private final Label totalCoursesValue = new Label("0");

    private final HBox chartsRow;
    private final VBox activityList;
    private PieChart pieChart;
    private BarChart<String, Number> barChart;

    public DashboardView() {
        container = new VBox(25);
        container.setPadding(new Insets(30));
        container.getStyleClass().add("dashboard-container");

        // Page title
        Label pageTitle = new Label("\uD83D\uDCCA Dashboard Overview");
        pageTitle.getStyleClass().add("page-title");

        // === STAT CARDS ROW ===
        HBox statsRow = new HBox(20);
        statsRow.setAlignment(Pos.CENTER_LEFT);

        statsRow.getChildren().addAll(
            createStatCard("\uD83C\uDF93", "Total Students", totalStudentsValue, "stat-card-blue"),
            createStatCard("\uD83D\uDCCA", "Average Marks", avgMarksValue, "stat-card-green"),
            createStatCard("\uD83C\uDFC6", "Top Performer", topPerformerValue, "stat-card-purple"),
            createStatCard("\uD83D\uDCDA", "Total Courses", totalCoursesValue, "stat-card-orange")
        );

        // Make cards grow equally
        for (var node : statsRow.getChildren()) {
            HBox.setHgrow(node, Priority.ALWAYS);
        }

        // === CHARTS ROW ===
        chartsRow = new HBox(20);
        chartsRow.setAlignment(Pos.TOP_CENTER);

        // Pie chart placeholder
        VBox pieBox = new VBox(10);
        pieBox.getStyleClass().add("chart-card");
        pieBox.setPadding(new Insets(20));
        Label pieTitle = new Label("Student Distribution by Course");
        pieTitle.getStyleClass().add("chart-title");
        pieChart = new PieChart();
        pieChart.setLabelsVisible(true);
        pieChart.setLegendVisible(true);
        pieChart.setPrefSize(400, 300);
        pieBox.getChildren().addAll(pieTitle, pieChart);
        HBox.setHgrow(pieBox, Priority.ALWAYS);

        // Bar chart placeholder
        VBox barBox = new VBox(10);
        barBox.getStyleClass().add("chart-card");
        barBox.setPadding(new Insets(20));
        Label barTitle = new Label("Average Marks by Course");
        barTitle.getStyleClass().add("chart-title");

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Course");
        NumberAxis yAxis = new NumberAxis(0, 100, 10);
        yAxis.setLabel("Avg Marks");
        barChart = new BarChart<>(xAxis, yAxis);
        barChart.setLegendVisible(false);
        barChart.setPrefSize(400, 300);
        barBox.getChildren().addAll(barTitle, barChart);
        HBox.setHgrow(barBox, Priority.ALWAYS);

        chartsRow.getChildren().addAll(pieBox, barBox);

        // === RECENT ACTIVITY ===
        VBox activityCard = new VBox(10);
        activityCard.getStyleClass().add("chart-card");
        activityCard.setPadding(new Insets(20));
        Label actTitle = new Label("\uD83D\uDD53 Recent Activity");
        actTitle.getStyleClass().add("chart-title");
        activityList = new VBox(5);
        activityList.getStyleClass().add("activity-list");

        // Placeholder
        Label noActivity = new Label("No recent activity");
        noActivity.getStyleClass().add("activity-item-empty");
        activityList.getChildren().add(noActivity);

        activityCard.getChildren().addAll(actTitle, activityList);

        container.getChildren().addAll(pageTitle, statsRow, chartsRow, activityCard);

        root = new ScrollPane(container);
        root.setFitToWidth(true);
        root.getStyleClass().add("content-scroll");
    }

    private VBox createStatCard(String icon, String label, Label valueLabel, String styleClass) {
        VBox card = new VBox(8);
        card.getStyleClass().addAll("stat-card", styleClass);
        card.setPadding(new Insets(20));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setMinWidth(180);

        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 28px;");

        Label titleLabel = new Label(label);
        titleLabel.getStyleClass().add("stat-label");

        valueLabel.getStyleClass().add("stat-value");

        card.getChildren().addAll(iconLabel, titleLabel, valueLabel);
        return card;
    }

    /**
     * Update the statistics cards.
     */
    public void updateStats(int totalStudents, double avgMarks, Student topPerformer, int totalCourses) {
        totalStudentsValue.setText(String.valueOf(totalStudents));
        avgMarksValue.setText(String.format("%.1f", avgMarks));
        topPerformerValue.setText(topPerformer != null ? topPerformer.getName() : "-");
        totalCoursesValue.setText(String.valueOf(totalCourses));
    }

    /**
     * Update the pie chart with course distribution data.
     */
    public void updatePieChart(Map<String, Integer> courseDistribution) {
        pieChart.getData().clear();
        for (var entry : courseDistribution.entrySet()) {
            pieChart.getData().add(new PieChart.Data(entry.getKey() + " (" + entry.getValue() + ")", entry.getValue()));
        }
    }

    /**
     * Update the bar chart with average marks by course.
     */
    public void updateBarChart(Map<String, Double> avgMarksByCourse) {
        barChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Avg Marks");
        for (var entry : avgMarksByCourse.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        barChart.getData().add(series);
    }

    /**
     * Update the recent activity feed.
     */
    public void updateActivityLog(List<ActivityLog> activities) {
        activityList.getChildren().clear();
        if (activities.isEmpty()) {
            Label empty = new Label("No recent activity");
            empty.getStyleClass().add("activity-item-empty");
            activityList.getChildren().add(empty);
            return;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, HH:mm");
        for (ActivityLog log : activities) {
            HBox item = new HBox(10);
            item.getStyleClass().add("activity-item");
            item.setAlignment(Pos.CENTER_LEFT);
            item.setPadding(new Insets(8, 12, 8, 12));

            String icon = switch (log.getAction()) {
                case "ADD" -> "\u2795";
                case "UPDATE" -> "\u270F";
                case "DELETE" -> "\uD83D\uDDD1";
                case "LOGIN" -> "\uD83D\uDD13";
                case "EXPORT" -> "\uD83D\uDCE4";
                case "IMPORT" -> "\uD83D\uDCE5";
                default -> "\u2022";
            };

            Label iconLbl = new Label(icon);
            iconLbl.setMinWidth(25);

            Label detailLbl = new Label(log.getDetails());
            detailLbl.getStyleClass().add("activity-detail");
            HBox.setHgrow(detailLbl, Priority.ALWAYS);

            Label timeLbl = new Label(log.getCreatedAt() != null ? sdf.format(log.getCreatedAt()) : "");
            timeLbl.getStyleClass().add("activity-time");

            item.getChildren().addAll(iconLbl, detailLbl, timeLbl);
            activityList.getChildren().add(item);
        }
    }

    public ScrollPane getRoot() { return root; }
}
