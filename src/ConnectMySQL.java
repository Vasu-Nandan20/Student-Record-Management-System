import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectMySQL {
    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/?user=root",
                "root",
                "your_password"
            );

            System.out.println("✅ Connected Successfully!");

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}