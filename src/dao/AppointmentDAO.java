package dao;

import util.DBConnection;
import java.sql.*;

public class AppointmentDAO {

    public static int insert(String name, String mobile, String service, String otherService,
                             String date, String time, String message) {

        String sql = "INSERT INTO appointments (name, mobile, service, other_service, preferred_date, preferred_time, message) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, name);
            ps.setString(2, mobile);
            ps.setString(3, service);
            ps.setString(4, otherService);
            ps.setString(5, date);
            ps.setString(6, time);
            ps.setString(7, message);

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
}
