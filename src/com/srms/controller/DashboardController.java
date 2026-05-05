package com.srms.controller;

import com.srms.dao.ActivityLogDAO;
import com.srms.model.ActivityLog;
import com.srms.model.Student;
import com.srms.service.StudentService;
import com.srms.view.DashboardView;
import java.util.List;
import java.util.Map;

/**
 * Controller for the dashboard view.
 * Loads statistics, charts, and activity data.
 */
public class DashboardController {
    private final DashboardView view;
    private final StudentService studentService;
    private final ActivityLogDAO activityLogDAO;

    public DashboardController() {
        this.view = new DashboardView();
        this.studentService = new StudentService();
        this.activityLogDAO = new ActivityLogDAO();
    }

    /**
     * Refresh all dashboard data.
     */
    public void refresh() {
        try {
            // Stats
            int totalStudents = studentService.getStudentCount();
            double avgMarks = studentService.getAverageMarks();
            Student topPerformer = studentService.getTopPerformer();
            List<String> courses = studentService.getAllCourses();

            view.updateStats(totalStudents, avgMarks, topPerformer, courses.size());

            // Charts
            Map<String, Integer> courseDist = studentService.getCourseDistribution();
            view.updatePieChart(courseDist);

            Map<String, Double> avgByCourse = studentService.getAverageMarksByCourse();
            view.updateBarChart(avgByCourse);

            // Activity log
            List<ActivityLog> activities = activityLogDAO.getRecentActivities(10);
            view.updateActivityLog(activities);

        } catch (Exception e) {
            System.err.println("[Dashboard] Error refreshing: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public DashboardView getView() { return view; }
}
