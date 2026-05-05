package com.srms.dao;

import com.srms.config.DatabaseConfig;
import com.srms.model.User;
import java.sql.*;

/**
 * Data Access Object for User authentication operations.
 */
public class UserDAO {

    /**
     * Authenticate a user by username and password hash.
     */
    public User authenticate(String username, String passwordHash) throws SQLException {
        String query = "SELECT * FROM users WHERE username = ? AND password_hash = ?";
        PreparedStatement pst = DatabaseConfig.getConnection().prepareStatement(query);
        pst.setString(1, username);
        pst.setString(2, passwordHash);
        ResultSet rs = pst.executeQuery();
        User user = null;
        if (rs.next()) {
            user = new User();
            user.setId(rs.getInt("id"));
            user.setUsername(rs.getString("username"));
            user.setPasswordHash(rs.getString("password_hash"));
            user.setRole(rs.getString("role"));
            user.setCreatedAt(rs.getTimestamp("created_at"));
        }
        rs.close();
        pst.close();
        return user;
    }

    /**
     * Create a new user.
     */
    public boolean createUser(User user) throws SQLException {
        String query = "INSERT INTO users (username, password_hash, role) VALUES (?, ?, ?)";
        PreparedStatement pst = DatabaseConfig.getConnection().prepareStatement(query);
        pst.setString(1, user.getUsername());
        pst.setString(2, user.getPasswordHash());
        pst.setString(3, user.getRole());
        int rows = pst.executeUpdate();
        pst.close();
        return rows > 0;
    }
}
