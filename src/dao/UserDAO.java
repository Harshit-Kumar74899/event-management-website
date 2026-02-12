package dao;

import util.DBConnection;
import java.sql.*;

public class UserDAO {


    public static String getNameByEmail(String email) {

    Connection con = DBConnection.getConnection();
    if (con == null) return null;

    try {
        String sql = "SELECT name FROM users WHERE email=?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, email);

        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getString("name");
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return null;
}


    // 🔹 CHECK EMAIL EXISTS
    public static boolean emailExists(String email) {

        Connection con = DBConnection.getConnection();

        // ✅ SAFETY CHECK (VERY IMPORTANT)
        if (con == null) {
            System.out.println("❌ DB Connection failed in emailExists()");
            return false;
        }

        try {
            String sql = "SELECT id FROM users WHERE email=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, email);

            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (Exception e) {
            System.out.println("❌ Error while checking email");
            e.printStackTrace();
            return false;
        }
    }

    // 🔹 REGISTER USER
    public static boolean register(String name, String email, String password) {

        Connection con = DBConnection.getConnection();

        // ✅ SAFETY CHECK
        if (con == null) {
            System.out.println("❌ DB Connection failed in register()");
            return false;
        }

        try {
            String sql = "INSERT INTO users(name,email,password) VALUES(?,?,?)";
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, password);

            ps.executeUpdate();
            return true;

        } catch (Exception e) {
            System.out.println("❌ Error while inserting user");
            e.printStackTrace();
            return false;
        }
    }

    // 🔹 LOGIN
    public static boolean login(String email, String password) {

        Connection con = DBConnection.getConnection();

        // ✅ SAFETY CHECK
        if (con == null) {
            System.out.println("❌ DB Connection failed in login()");
            return false;
        }

        try {
            String sql = "SELECT * FROM users WHERE email=? AND password=?";
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (Exception e) {
            System.out.println("❌ Error while login");
            e.printStackTrace();
            return false;
        }
    }
}