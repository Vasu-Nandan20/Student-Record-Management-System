import java.sql.*;
import java.util.Scanner;

public class StudentManagement {

    static final String URL = "jdbc:mysql://localhost:3306/student_db";
    static final String USER = "root";
    static final String PASSWORD = "Password";

    static Connection con;
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(URL, USER, PASSWORD);

            int choice;

            do {
                System.out.println("\n===== STUDENT MANAGEMENT SYSTEM =====");
                System.out.println("1. Add Student");
                System.out.println("2. View Students");
                System.out.println("3. Update Student");
                System.out.println("4. Delete Student");
                System.out.println("5. Exit");

                System.out.print("Enter choice: ");
                choice = sc.nextInt();

                switch(choice) {
                    case 1: addStudent(); break;
                    case 2: viewStudents(); break;
                    case 3: updateStudent(); break;
                    case 4: deleteStudent(); break;
                    case 5: System.out.println("Exiting..."); break;
                    default: System.out.println("Invalid choice!");
                }

            } while(choice != 5);

            con.close();

        } catch(Exception e) {
            System.out.println(e);
        }
    }

    // ADD
    static void addStudent() throws SQLException {
        System.out.print("Name: ");
        String name = sc.next();

        System.out.print("Age: ");
        int age = sc.nextInt();

        System.out.print("Course: ");
        sc.nextLine(); 
        String course = sc.nextLine();

        System.out.print("Marks: ");
        double marks = sc.nextDouble();

        String query = "INSERT INTO students(name, age, course, marks) VALUES (?, ?, ?, ?)";
        PreparedStatement pst = con.prepareStatement(query);

        pst.setString(1, name);
        pst.setInt(2, age);
        pst.setString(3, course);
        pst.setDouble(4, marks);

        pst.executeUpdate();
        System.out.println("✅ Student Added!");
    }

    // VIEW
    static void viewStudents() throws SQLException {
        String query = "SELECT * FROM students";
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(query);

        System.out.println("\nID\tName\tAge\tCourse\tMarks");

        while(rs.next()) {
            System.out.println(
                rs.getInt("id") + "\t" +
                rs.getString("name") + "\t" +
                rs.getInt("age") + "\t" +
                rs.getString("course") + "\t" +
                rs.getDouble("marks")
            );
        }
    }

    // UPDATE
    static void updateStudent() throws SQLException {
        System.out.print("Enter ID: ");
        int id = sc.nextInt();

        System.out.print("New Marks: ");
        double marks = sc.nextDouble();

        String query = "UPDATE students SET marks=? WHERE id=?";
        PreparedStatement pst = con.prepareStatement(query);

        pst.setDouble(1, marks);
        pst.setInt(2, id);

        if(pst.executeUpdate() > 0)
            System.out.println("✅ Updated!");
        else
            System.out.println("❌ Not Found!");
    }

    // DELETE
    static void deleteStudent() throws SQLException {
        System.out.print("Enter ID: ");
        int id = sc.nextInt();

        String query = "DELETE FROM students WHERE id=?";
        PreparedStatement pst = con.prepareStatement(query);

        pst.setInt(1, id);

        if(pst.executeUpdate() > 0)
            System.out.println("✅ Deleted!");
        else
            System.out.println("❌ Not Found!");
    }
}