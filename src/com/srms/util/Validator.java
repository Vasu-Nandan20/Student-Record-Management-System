package com.srms.util;

/**
 * Input validation utility with descriptive error messages.
 * Returns null on valid input, error message string on invalid.
 */
public class Validator {

    public static String validateName(String name) {
        if (name == null || name.trim().isEmpty()) return "Name is required";
        if (name.trim().length() < 2) return "Name must be at least 2 characters";
        if (name.trim().length() > 50) return "Name must be under 50 characters";
        if (!name.trim().matches("[a-zA-Z\\s.'-]+")) return "Name contains invalid characters";
        return null;
    }

    public static String validateAge(String ageStr) {
        if (ageStr == null || ageStr.trim().isEmpty()) return "Age is required";
        try {
            int age = Integer.parseInt(ageStr.trim());
            if (age < 5 || age > 100) return "Age must be between 5 and 100";
        } catch (NumberFormatException e) {
            return "Age must be a valid number";
        }
        return null;
    }

    public static String validateCourse(String course) {
        if (course == null || course.trim().isEmpty()) return "Course is required";
        if (course.trim().length() > 50) return "Course must be under 50 characters";
        return null;
    }

    public static String validateMarks(String marksStr) {
        if (marksStr == null || marksStr.trim().isEmpty()) return "Marks is required";
        try {
            double marks = Double.parseDouble(marksStr.trim());
            if (marks < 0 || marks > 100) return "Marks must be between 0 and 100";
        } catch (NumberFormatException e) {
            return "Marks must be a valid number";
        }
        return null;
    }

    public static String validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) return null; // Email is optional
        if (!email.trim().matches("^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            return "Invalid email format";
        }
        return null;
    }

    public static String validatePhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) return null; // Phone is optional
        if (!phone.trim().matches("^[+]?[0-9]{7,15}$")) {
            return "Invalid phone number";
        }
        return null;
    }
}
