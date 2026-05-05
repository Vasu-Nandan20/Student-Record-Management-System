package com.srms.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Singleton database configuration and connection manager.
 * Manages MySQL connections for the entire application.
 */
public class DatabaseConfig {
    private static final String URL = "jdbc:mysql://localhost:3306/student_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "Password";

    private static Connection connection;

    private DatabaseConfig() {}

    /**
     * Get a database connection (creates one if none exists or if closed).
     */
    public static synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("[DB] Connected to MySQL successfully.");
            } catch (ClassNotFoundException e) {
                throw new SQLException("MySQL JDBC Driver not found.", e);
            }
        }
        return connection;
    }

    /**
     * Run the database schema migration to ensure all tables exist.
     */
    public static void runMigrations() throws SQLException {
        Connection conn = getConnection();
        Statement st = conn.createStatement();

        // Add email column if not exists
        try {
            st.executeUpdate("ALTER TABLE students ADD COLUMN email VARCHAR(100) DEFAULT NULL");
        } catch (SQLException e) {
            // Column already exists — ignore
        }

        // Add phone column if not exists
        try {
            st.executeUpdate("ALTER TABLE students ADD COLUMN phone VARCHAR(15) DEFAULT NULL");
        } catch (SQLException e) {
            // Column already exists — ignore
        }

        // Create users table
        st.executeUpdate("""
            CREATE TABLE IF NOT EXISTS users (
                id INT AUTO_INCREMENT PRIMARY KEY,
                username VARCHAR(50) NOT NULL UNIQUE,
                password_hash VARCHAR(255) NOT NULL,
                role ENUM('ADMIN', 'VIEWER') DEFAULT 'ADMIN',
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """);

        // Create activity_log table
        st.executeUpdate("""
            CREATE TABLE IF NOT EXISTS activity_log (
                id INT AUTO_INCREMENT PRIMARY KEY,
                action VARCHAR(255) NOT NULL,
                details TEXT,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """);

        // Insert default admin (admin / admin123) — SHA-256 hash
        try {
            st.executeUpdate("""
                INSERT INTO users (username, password_hash, role)
                VALUES ('admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'ADMIN')
            """);
        } catch (SQLException e) {
            // Admin already exists — ignore duplicate
        }

        st.close();
        System.out.println("[DB] Migrations completed successfully.");
    }

    /**
     * Close the database connection.
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[DB] Connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
