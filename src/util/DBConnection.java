package util;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    // 🔹 Render ke Environment Variables se aayenge.
    //    Local PC pe agar ye set nahi hain, to default localhost wala use hoga.
    private static final String DB_URL = getEnv("DB_URL", "jdbc:mysql://localhost:3306/wedding_db");
    private static final String DB_USER = getEnv("DB_USER", "root");
    private static final String DB_PASSWORD = getEnv("DB_PASSWORD", "1234");

    private static String getEnv(String key, String defaultValue) {
        String value = System.getenv(key);
        return (value == null || value.isEmpty()) ? defaultValue : value;
    }

    public static Connection getConnection() {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            System.out.println("✅ DB Connected Successfully");
            return con;

        } catch (Exception e) {
            System.out.println("❌ DB Connection Error");
            e.printStackTrace();
            return null;
        }
    }
}
