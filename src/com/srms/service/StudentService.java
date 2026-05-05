package com.srms.service;

import com.srms.dao.ActivityLogDAO;
import com.srms.dao.StudentDAO;
import com.srms.model.Student;
import java.sql.SQLException;
import java.util.*;

/**
 * Business logic layer for student operations.
 * Wraps DAO calls with activity logging and validation.
 */
public class StudentService {
    private final StudentDAO studentDAO = new StudentDAO();
    private final ActivityLogDAO logDAO = new ActivityLogDAO();

    public List<Student> getAllStudents() throws SQLException {
        return studentDAO.getAllStudents();
    }

    public Student getStudentById(int id) throws SQLException {
        return studentDAO.getStudentById(id);
    }

    public List<Student> searchStudents(String query) throws SQLException {
        return studentDAO.searchStudents(query);
    }

    /**
     * Get filtered, sorted, paginated students.
     */
    public List<Student> getStudentsPaginated(String search, String course, double minMarks, double maxMarks,
                                               String sortBy, boolean ascending, int page, int pageSize) throws SQLException {
        return studentDAO.getStudentsFiltered(search, course, minMarks, maxMarks, sortBy, ascending, page, pageSize);
    }

    public int getTotalPages(String search, String course, double minMarks, double maxMarks, int pageSize) throws SQLException {
        int total = studentDAO.countFiltered(search, course, minMarks, maxMarks);
        return Math.max(1, (int) Math.ceil((double) total / pageSize));
    }

    public int getTotalCount(String search, String course, double minMarks, double maxMarks) throws SQLException {
        return studentDAO.countFiltered(search, course, minMarks, maxMarks);
    }

    public boolean addStudent(Student student) throws SQLException {
        boolean result = studentDAO.addStudent(student);
        if (result) {
            logDAO.logActivity("ADD", "Added student: " + student.getName() + " (" + student.getCourse() + ")");
        }
        return result;
    }

    public boolean updateStudent(Student student) throws SQLException {
        boolean result = studentDAO.updateStudent(student);
        if (result) {
            logDAO.logActivity("UPDATE", "Updated student ID " + student.getId() + ": " + student.getName());
        }
        return result;
    }

    public boolean deleteStudent(int id) throws SQLException {
        Student s = studentDAO.getStudentById(id);
        boolean result = studentDAO.deleteStudent(id);
        if (result && s != null) {
            logDAO.logActivity("DELETE", "Deleted student: " + s.getName() + " (ID: " + id + ")");
        }
        return result;
    }

    // Dashboard stats
    public int getStudentCount() throws SQLException { return studentDAO.getStudentCount(); }
    public double getAverageMarks() throws SQLException { return studentDAO.getAverageMarks(); }
    public Student getTopPerformer() throws SQLException { return studentDAO.getTopPerformer(); }
    public List<String> getAllCourses() throws SQLException { return studentDAO.getAllCourses(); }
    public Map<String, Integer> getCourseDistribution() throws SQLException { return studentDAO.getCourseDistribution(); }
    public Map<String, Double> getAverageMarksByCourse() throws SQLException { return studentDAO.getAverageMarksByCourse(); }
}
