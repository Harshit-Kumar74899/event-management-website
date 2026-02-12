package handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.UserDAO;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class VerifyOtpHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        String body = new String(
                exchange.getRequestBody().readAllBytes(),
                StandardCharsets.UTF_8
        );

        String email = null;
        String userOtp = null;

        for (String pair : body.split("&")) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2) {
                String key = kv[0];
                String value = URLDecoder.decode(kv[1], "UTF-8");

                if (key.equals("email")) email = value.trim().toLowerCase();
                if (key.equals("otp")) userOtp = value.trim();
            }
        }

        String storedOtp = SignupHandler.otpStore.get(email);

        System.out.println("EMAIL = " + email);
        System.out.println("USER OTP = " + userOtp);
        System.out.println("STORED OTP = " + storedOtp);

        /* ❌ WRONG OTP */
        if (storedOtp == null || !storedOtp.equals(userOtp)) {
            exchange.sendResponseHeaders(200, 5);
            exchange.getResponseBody().write("wrong".getBytes());
            exchange.close();
            return;
        }

        /* ✅ OTP CORRECT */
        SignupHandler.TempUser user =
                SignupHandler.tempUserStore.get(email);

        if (user != null && !UserDAO.emailExists(email)) {
            UserDAO.register(user.name, user.email, user.password);
            System.out.println("User inserted");
        }

        SignupHandler.otpStore.remove(email);
        SignupHandler.tempUserStore.remove(email);

        exchange.sendResponseHeaders(200, 7);
        exchange.getResponseBody().write("success".getBytes());
        exchange.close();
    }
}
