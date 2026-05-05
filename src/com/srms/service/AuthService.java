package com.srms.service;

import com.srms.dao.ActivityLogDAO;
import com.srms.dao.UserDAO;
import com.srms.model.User;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

/**
 * Authentication service handling login and password hashing.
 */
public class AuthService {
    private final UserDAO userDAO = new UserDAO();
    private final ActivityLogDAO logDAO = new ActivityLogDAO();

    /**
     * Attempt to login with username and plain-text password.
     * Returns the User on success, null on failure.
     */
    public User login(String username, String password) throws SQLException {
        String hash = hashPassword(password);
        User user = userDAO.authenticate(username, hash);
        if (user != null) {
            logDAO.logActivity("LOGIN", "User '" + username + "' logged in successfully.");
        } else {
            logDAO.logActivity("LOGIN_FAILED", "Failed login attempt for username: " + username);
        }
        return user;
    }

    /**
     * SHA-256 hash of the given password string.
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}
