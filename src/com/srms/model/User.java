package com.srms.model;

import java.sql.Timestamp;

/**
 * User model for admin authentication.
 * Maps to the 'users' table in MySQL.
 */
public class User {
    private int id;
    private String username;
    private String passwordHash;
    private String role;
    private Timestamp createdAt;

    public User() {}

    public User(String username, String passwordHash, String role) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public String getRole() { return role; }
    public Timestamp getCreatedAt() { return createdAt; }

    public void setId(int id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void setRole(String role) { this.role = role; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
