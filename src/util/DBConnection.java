package util;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    public static Connection getConnection() {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/wedding_db",
                    "root",
                    "1234"
            );

            System.out.println("✅ DB Connected Successfully");
            return con;

        } catch (Exception e) {
            System.out.println("❌ DB Connection Error");
            e.printStackTrace();
            return null;
        }
    }
}
