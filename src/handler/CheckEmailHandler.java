package handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import util.DBConnection;
import java.io.*;
import java.sql.*;

public class CheckEmailHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String response = "";

        try {
            // 📥 Read email from request
            BufferedReader br = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
            String email = br.readLine();

            // 🔌 DB connection
            Connection con = DBConnection.getConnection();

            PreparedStatement ps = con.prepareStatement("SELECT * FROM users WHERE email=?");
            ps.setString(1, email);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                response = "EXISTS";
            } else {
                response = "NOT_FOUND";
            }

            // ❗ Close resources
            rs.close();
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