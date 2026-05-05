package com.srms.model;

/**
 * Student model class representing a student record.
 * Maps to the 'students' table in MySQL.
 */
public class Student {
    private int id;
    private String name;
    private int age;
    private String course;
    private double marks;
    private String email;
    private String phone;

    public Student() {}

    public Student(String name, int age, String course, double marks, String email, String phone) {
        this.name = name;
        this.age = age;
        this.course = course;
        this.marks = marks;
        this.email = email;
        this.phone = phone;
    }

    public Student(int id, String name, int age, String course, double marks, String email, String phone) {
        this(name, age, course, marks, email, phone);
        this.id = id;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getCourse() { return course; }
    public double getMarks() { return marks; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setAge(int age) { this.age = age; }
    public void setCourse(String course) { this.course = course; }
    public void setMarks(double marks) { this.marks = marks; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }

    @Override
    public String toString() {
        return String.format("Student[id=%d, name=%s, course=%s, marks=%.1f]", id, name, course, marks);
    }
}
