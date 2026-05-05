package com.srms.model;

import java.sql.Timestamp;

/**
 * ActivityLog model for tracking recent actions.
 * Maps to the 'activity_log' table in MySQL.
 */
public class ActivityLog {
    private int id;
    private String action;
    private String details;
    private Timestamp createdAt;

    public ActivityLog() {}

    public ActivityLog(String action, String details) {
        this.action = action;
        this.details = details;
    }

    public int getId() { return id; }
    public String getAction() { return action; }
    public String getDetails() { return details; }
    public Timestamp getCreatedAt() { return createdAt; }

    public void setId(int id) { this.id = id; }
    public void setAction(String action) { this.action = action; }
    public void setDetails(String details) { this.details = details; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
