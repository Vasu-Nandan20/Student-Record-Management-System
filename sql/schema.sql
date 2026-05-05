-- ============================================
-- SRMS Database Schema Upgrade
-- Run this against your existing student_db
-- ============================================

USE student_db;

-- Add email and phone columns to existing students table
-- Using a procedure to safely add columns
DELIMITER //
CREATE PROCEDURE IF NOT EXISTS upgrade_students_table()
BEGIN
    IF NOT EXISTS (SELECT * FROM information_schema.columns 
                   WHERE table_schema = 'student_db' AND table_name = 'students' AND column_name = 'email') THEN
        ALTER TABLE students ADD COLUMN email VARCHAR(100) DEFAULT NULL;
    END IF;
    IF NOT EXISTS (SELECT * FROM information_schema.columns 
                   WHERE table_schema = 'student_db' AND table_name = 'students' AND column_name = 'phone') THEN
        ALTER TABLE students ADD COLUMN phone VARCHAR(15) DEFAULT NULL;
    END IF;
END //
DELIMITER ;

CALL upgrade_students_table();
DROP PROCEDURE IF EXISTS upgrade_students_table;

-- Users table for admin login
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'VIEWER') DEFAULT 'ADMIN',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Activity log table
CREATE TABLE IF NOT EXISTS activity_log (
    id INT AUTO_INCREMENT PRIMARY KEY,
    action VARCHAR(255) NOT NULL,
    details TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert default admin user (username: admin, password: admin123)
-- SHA-256 hash of 'admin123'
INSERT IGNORE INTO users (username, password_hash, role) 
VALUES ('admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'ADMIN');
