package com.srms.dao;

import com.srms.config.DatabaseConfig;
import com.srms.model.Student;
import java.sql.*;
import java.util.*;

/**
 * Data Access Object for Student CRUD operations.
 * All database queries for the students table are centralized here.
 */
public class StudentDAO {

    /**
     * Get all students ordered by ID.
     */
    public List<Student> getAllStudents() throws SQLException {
        List<Student> students = new ArrayList<>();
        String query = "SELECT * FROM students ORDER BY id";
        Statement st = DatabaseConfig.getConnection().createStatement();
        ResultSet rs = st.executeQuery(query);
        while (rs.next()) {
            students.add(mapResultSet(rs));
        }
        rs.close();
        st.close();
        return students;
    }

    /**
     * Get a single student by ID.
     */
    public Student getStudentById(int id) throws SQLException {
        String query = "SELECT * FROM students WHERE id = ?";
        PreparedStatement pst = DatabaseConfig.getConnection().prepareStatement(query);
        pst.setInt(1, id);
        ResultSet rs = pst.executeQuery();
        Student student = null;
        if (rs.next()) {
            student = mapResultSet(rs);
        }
        rs.close();
        pst.close();
        return student;
    }

    /**
     * Search students by name or ID (partial match).
     */
    public List<Student> searchStudents(String searchQuery) throws SQLException {
        List<Student> students = new ArrayList<>();
        String query = "SELECT * FROM students WHERE name LIKE ? OR CAST(id AS CHAR) LIKE ? ORDER BY id";
        PreparedStatement pst = DatabaseConfig.getConnection().prepareStatement(query);
        String pattern = "%" + searchQuery + "%";
        pst.setString(1, pattern);
        pst.setString(2, pattern);
        ResultSet rs = pst.executeQuery();
        while (rs.next()) {
            students.add(mapResultSet(rs));
        }
        rs.close();
        pst.close();
        return students;
    }

    /**
     * Filter students by course.
     */
    public List<Student> filterByCourse(String course) throws SQLException {
        List<Student> students = new ArrayList<>();
        String query = "SELECT * FROM students WHERE course = ? ORDER BY id";
        PreparedStatement pst = DatabaseConfig.getConnection().prepareStatement(query);
        pst.setString(1, course);
        ResultSet rs = pst.executeQuery();
        while (rs.next()) {
            students.add(mapResultSet(rs));
        }
        rs.close();
        pst.close();
        return students;
    }

    /**
     * Filter students by marks range.
     */
    public List<Student> filterByMarksRange(double min, double max) throws SQLException {
        List<Student> students = new ArrayList<>();
        String query = "SELECT * FROM students WHERE marks BETWEEN ? AND ? ORDER BY id";
        PreparedStatement pst = DatabaseConfig.getConnection().prepareStatement(query);
        pst.setDouble(1, min);
        pst.setDouble(2, max);
        ResultSet rs = pst.executeQuery();
        while (rs.next()) {
            students.add(mapResultSet(rs));
        }
        rs.close();
        pst.close();
        return students;
    }

    /**
     * Advanced query: search + filter + sort + paginate.
     */
    public List<Student> getStudentsFiltered(String search, String course, double minMarks, double maxMarks,
                                              String sortBy, boolean ascending, int page, int pageSize) throws SQLException {
        List<Student> students = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM students WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (search != null && !search.trim().isEmpty()) {
            sql.append(" AND (name LIKE ? OR CAST(id AS CHAR) LIKE ? OR email LIKE ?)");
            String pattern = "%" + search.trim() + "%";
            params.add(pattern);
            params.add(pattern);
            params.add(pattern);
        }
        if (course != null && !course.trim().isEmpty() && !course.equals("All")) {
            sql.append(" AND course = ?");
            params.add(course);
        }
        if (minMarks >= 0) {
            sql.append(" AND marks >= ?");
            params.add(minMarks);
        }
        if (maxMarks > 0 && maxMarks <= 100) {
            sql.append(" AND marks <= ?");
            params.add(maxMarks);
        }

        // Sort
        String validSort = switch (sortBy != null ? sortBy : "id") {
            case "name" -> "name";
            case "age" -> "age";
            case "course" -> "course";
            case "marks" -> "marks";
            default -> "id";
        };
        sql.append(" ORDER BY ").append(validSort).append(ascending ? " ASC" : " DESC");

        // Pagination
        sql.append(" LIMIT ? OFFSET ?");
        params.add(pageSize);
        params.add((page - 1) * pageSize);

        PreparedStatement pst = DatabaseConfig.getConnection().prepareStatement(sql.toString());
        for (int i = 0; i < params.size(); i++) {
            Object param = params.get(i);
            if (param instanceof String) pst.setString(i + 1, (String) param);
            else if (param instanceof Double) pst.setDouble(i + 1, (Double) param);
            else if (param instanceof Integer) pst.setInt(i + 1, (Integer) param);
        }

        ResultSet rs = pst.executeQuery();
        while (rs.next()) {
            students.add(mapResultSet(rs));
        }
        rs.close();
        pst.close();
        return students;
    }

    /**
     * Count total records matching filters (for pagination).
     */
    public int countFiltered(String search, String course, double minMarks, double maxMarks) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM students WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (search != null && !search.trim().isEmpty()) {
            sql.append(" AND (name LIKE ? OR CAST(id AS CHAR) LIKE ? OR email LIKE ?)");
            String pattern = "%" + search.trim() + "%";
            params.add(pattern);
            params.add(pattern);
            params.add(pattern);
        }
        if (course != null && !course.trim().isEmpty() && !course.equals("All")) {
            sql.append(" AND course = ?");
            params.add(course);
        }
        if (minMarks >= 0) {
            sql.append(" AND marks >= ?");
            params.add(minMarks);
        }
        if (maxMarks > 0 && maxMarks <= 100) {
            sql.append(" AND marks <= ?");
            params.add(maxMarks);
        }

        PreparedStatement pst = DatabaseConfig.getConnection().prepareStatement(sql.toString());
        for (int i = 0; i < params.size(); i++) {
            Object param = params.get(i);
            if (param instanceof String) pst.setString(i + 1, (String) param);
            else if (param instanceof Double) pst.setDouble(i + 1, (Double) param);
            else if (param instanceof Integer) pst.setInt(i + 1, (Integer) param);
        }

        ResultSet rs = pst.executeQuery();
        int count = 0;
        if (rs.next()) count = rs.getInt(1);
        rs.close();
        pst.close();
        return count;
    }

    /**
     * Add a new student.
     */
    public boolean addStudent(Student s) throws SQLException {
        String query = "INSERT INTO students (name, age, course, marks, email, phone) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement pst = DatabaseConfig.getConnection().prepareStatement(query);
        pst.setString(1, s.getName());
        pst.setInt(2, s.getAge());
        pst.setString(3, s.getCourse());
        pst.setDouble(4, s.getMarks());
        pst.setString(5, s.getEmail());
        pst.setString(6, s.getPhone());
        int rows = pst.executeUpdate();
        pst.close();
        return rows > 0;
    }

    /**
     * Update an existing student.
     */
    public boolean updateStudent(Student s) throws SQLException {
        String query = "UPDATE students SET name=?, age=?, course=?, marks=?, email=?, phone=? WHERE id=?";
        PreparedStatement pst = DatabaseConfig.getConnection().prepareStatement(query);
        pst.setString(1, s.getName());
        pst.setInt(2, s.getAge());
        pst.setString(3, s.getCourse());
        pst.setDouble(4, s.getMarks());
        pst.setString(5, s.getEmail());
        pst.setString(6, s.getPhone());
        pst.setInt(7, s.getId());
        int rows = pst.executeUpdate();
        pst.close();
        return rows > 0;
    }

    /**
     * Delete a student by ID.
     */
    public boolean deleteStudent(int id) throws SQLException {
        String query = "DELETE FROM students WHERE id = ?";
        PreparedStatement pst = DatabaseConfig.getConnection().prepareStatement(query);
        pst.setInt(1, id);
        int rows = pst.executeUpdate();
        pst.close();
        return rows > 0;
    }

    // ===== Dashboard Statistics =====

    public int getStudentCount() throws SQLException {
        Statement st = DatabaseConfig.getConnection().createStatement();
        ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM students");
        int count = 0;
        if (rs.next()) count = rs.getInt(1);
        rs.close();
        st.close();
        return count;
    }

    public double getAverageMarks() throws SQLException {
        Statement st = DatabaseConfig.getConnection().createStatement();
        ResultSet rs = st.executeQuery("SELECT AVG(marks) FROM students");
        double avg = 0;
        if (rs.next()) avg = rs.getDouble(1);
        rs.close();
        st.close();
        return avg;
    }

    public Student getTopPerformer() throws SQLException {
        Statement st = DatabaseConfig.getConnection().createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM students ORDER BY marks DESC LIMIT 1");
        Student top = null;
        if (rs.next()) top = mapResultSet(rs);
        rs.close();
        st.close();
        return top;
    }

    public List<String> getAllCourses() throws SQLException {
        List<String> courses = new ArrayList<>();
        Statement st = DatabaseConfig.getConnection().createStatement();
        ResultSet rs = st.executeQuery("SELECT DISTINCT course FROM students ORDER BY course");
        while (rs.next()) {
            String c = rs.getString("course");
            if (c != null) courses.add(c);
        }
        rs.close();
        st.close();
        return courses;
    }

    /**
     * Get student count per course (for pie chart).
     */
    public Map<String, Integer> getCourseDistribution() throws SQLException {
        Map<String, Integer> dist = new LinkedHashMap<>();
        Statement st = DatabaseConfig.getConnection().createStatement();
        ResultSet rs = st.executeQuery("SELECT course, COUNT(*) as cnt FROM students GROUP BY course ORDER BY cnt DESC");
        while (rs.next()) {
            dist.put(rs.getString("course"), rs.getInt("cnt"));
        }
        rs.close();
        st.close();
        return dist;
    }

    /**
     * Get average marks per course (for bar chart).
     */
    public Map<String, Double> getAverageMarksByCourse() throws SQLException {
        Map<String, Double> avg = new LinkedHashMap<>();
        Statement st = DatabaseConfig.getConnection().createStatement();
        ResultSet rs = st.executeQuery("SELECT course, AVG(marks) as avg_marks FROM students GROUP BY course ORDER BY course");
        while (rs.next()) {
            avg.put(rs.getString("course"), rs.getDouble("avg_marks"));
        }
        rs.close();
        st.close();
        return avg;
    }

    // ===== Helper =====

    private Student mapResultSet(ResultSet rs) throws SQLException {
        return new Student(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getInt("age"),
            rs.getString("course"),
            rs.getDouble("marks"),
            rs.getString("email"),
            rs.getString("phone")
        );
    }
}
