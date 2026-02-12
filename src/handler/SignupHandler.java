package handler;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.UserDAO;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class SignupHandler implements HttpHandler {

    // 🔹 OTP STORE
    public static Map<String, String> otpStore = new HashMap<>();

    // 🔹 TEMP USER STORE
    public static Map<String, TempUser> tempUserStore = new HashMap<>();

    // 🔹 TEMP USER CLASS
    public static class TempUser {
        public String name;
        public String email;
        public String password;

        public TempUser(String name, String email, String password) {
            this.name = name;
            this.email = email;
            this.password = password;
        }
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        /* =======================
           GET → SIGNUP PAGE
           ======================= */
        if (exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            byte[] page = getClass()
                    .getResourceAsStream("/web/signup.html")
                    .readAllBytes();

            exchange.sendResponseHeaders(200, page.length);
            exchange.getResponseBody().write(page);
            exchange.close();
            return;
        }

        /* =======================
           ONLY POST ALLOWED
           ======================= */
        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            exchange.sendResponseHeaders(405, -1);
            exchange.close();
            return;
        }

        /* =======================
           READ FORM DATA
           ======================= */
        String body = new String(
                exchange.getRequestBody().readAllBytes(),
                StandardCharsets.UTF_8
        );

        String name = null;
        String email = null;
        String password = null;

        for (String pair : body.split("&")) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2) {
                String key = kv[0];
                String value = URLDecoder.decode(kv[1], "UTF-8");

                if (key.equals("name")) name = value;
                if (key.equals("email")) email = value;
                if (key.equals("password")) password = value;
            }
        }

        // ✅ NORMALIZE EMAIL
        if (email != null) {
            email = email.trim().toLowerCase();
        }

        System.out.println("Signup Data ? " + name + ", " + email);

        if (email == null || email.isEmpty()) {
            exchange.sendResponseHeaders(400, -1);
            exchange.close();
            return;
        }

        /* =======================
           USER ALREADY EXISTS
           ======================= */
        if (UserDAO.emailExists(email)) {

            System.out.println("⚠️ User already exists: " + email);

            // 🔥 fetch() ke liye plain text response
            exchange.sendResponseHeaders(200, 6);
            exchange.getResponseBody().write("exists".getBytes());
            exchange.close();
            return;
        }

        /* =======================
           GENERATE OTP (ONLY ONCE)
           ======================= */
        String otp;
        if (otpStore.containsKey(email)) {
            otp = otpStore.get(email);
        } else {
            otp = String.valueOf(new Random().nextInt(900000) + 100000);
            otpStore.put(email, otp);
        }

        System.out.println("===== DEBUG START =====");
        System.out.println("EMAIL = " + email);
        System.out.println("OTP   = " + otp);
        System.out.println("===== DEBUG END =====");

        // 🧠 STORE TEMP USER
        tempUserStore.put(email, new TempUser(name, email, password));

        // 📧 SEND OTP MAIL
        sendOtpEmail(email, otp);

        // 🔥 fetch() ke liye success signal
        exchange.sendResponseHeaders(200, 3);
        exchange.getResponseBody().write("otp".getBytes());
        exchange.close();
    }

    /* =======================
       OTP EMAIL FUNCTION
       ======================= */
    private void sendOtpEmail(String to, String otp) {

        System.out.println("📧 Sending OTP to: " + to);
        System.out.println("🔐 OTP is: " + otp);

        final String from = "harshitkumar.itm.092004@gmail.com";
        final String appPassword = "wgzqtynbybfsqyej";

        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtps");
        props.put("mail.smtps.host", "smtp.gmail.com");
        props.put("mail.smtps.port", "465");
        props.put("mail.smtps.auth", "true");
        props.put("mail.smtps.ssl.enable", "true");
        props.put("mail.smtps.ssl.trust", "smtp.gmail.com");

        try {
            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(from, appPassword);
                }
            });

            Message message = new MimeMessage(session);

            // 🔥 DISPLAY NAME (like "Jia from Unstop")
            message.setFrom(new InternetAddress(
                    from,
                    "Royal Wedding Decor • OTP Service"
            ));

            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(to)
            );

            message.setSubject("Your OTP Verification Code");

            message.setText(
                    "Hello,\n\n" +
                    "Your OTP for Royal Wedding Decor is: " + otp + "\n\n" +
                    "Please do not share this OTP with anyone.\n\n" +
                    "— Royal Wedding Decor"
            );

            Transport transport = session.getTransport("smtps");
            transport.connect();
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();

            System.out.println("✅ OTP MAIL SENT SUCCESSFULLY");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}