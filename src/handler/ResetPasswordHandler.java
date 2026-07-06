package handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import util.DBConnection;
import java.io.*;
import java.sql.*;

public class ResetPasswordHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String response = "";

        try {
            // 📥 Read data (email,password)
            BufferedReader br = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
            String data = br.readLine();

            String[] parts = data.split(",");
            String email = parts[0];
            String newPassword = parts[1];

            // 🔌 DB connection
            Connection con = DBConnection.getConnection();

            PreparedStatement ps = con.prepareStatement(
                "UPDATE users SET password=? WHERE email=?"
            );

            ps.setString(1, newPassword);
            ps.setString(2, email);

            int result = ps.executeUpdate();

            if (result > 0) {
                response = "SUCCESS";
            } else {
                response = "FAIL";
            }

            // ❗ Close resources
            ps.close();
            con.close();

        } catch (Exception e) {
            e.printStackTrace();
            response = "ERROR";
        }

        // 📤 Send response
        exchange.sendResponseHeaders(200, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}