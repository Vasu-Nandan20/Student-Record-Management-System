package com.srms.dao;

import com.srms.config.DatabaseConfig;
import com.srms.model.ActivityLog;
import java.sql.*;
import java.util.*;

/**
 * Data Access Object for activity logging.
 */
public class ActivityLogDAO {

    /**
     * Log a new activity.
     */
    public void logActivity(String action, String details) {
        try {
            String query = "INSERT INTO activity_log (action, details) VALUES (?, ?)";
            PreparedStatement pst = DatabaseConfig.getConnection().prepareStatement(query);
            pst.setString(1, action);
            pst.setString(2, details);
            pst.executeUpdate();
            pst.close();
        } catch (SQLException e) {
            System.err.println("[ActivityLog] Failed to log: " + e.getMessage());
        }
    }

    /**
     * Get recent activities (most recent first).
     */
    public List<ActivityLog> getRecentActivities(int limit) throws SQLException {
        List<ActivityLog> logs = new ArrayList<>();
        String query = "SELECT * FROM activity_log ORDER BY created_at DESC LIMIT ?";
        PreparedStatement pst = DatabaseConfig.getConnection().prepareStatement(query);
        pst.setInt(1, limit);
        ResultSet rs = pst.executeQuery();
        while (rs.next()) {
            ActivityLog log = new ActivityLog();
            log.setId(rs.getInt("id"));
            log.setAction(rs.getString("action"));
            log.setDetails(rs.getString("details"));
            log.setCreatedAt(rs.getTimestamp("created_at"));
            logs.add(log);
        }
        rs.close();
        pst.close();
        return logs;
    }

    /**
     * Clear all activity logs.
     */
    public void clearLogs() throws SQLException {
        Statement st = DatabaseConfig.getConnection().createStatement();
        st.executeUpdate("DELETE FROM activity_log");
        st.close();
    }
}
