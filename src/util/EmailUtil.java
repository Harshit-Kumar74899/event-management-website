package util;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

public class EmailUtil {

    // 🔹 Render ke Environment Variables se aate hain
    private static final String FROM_EMAIL = getEnv("FROM_EMAIL", "harshitkumar.itm.092004@gmail.com");
    private static final String APP_PASSWORD = getEnv("APP_PASSWORD", "");

    private static String getEnv(String key, String defaultValue) {
        String value = System.getenv(key);
        return (value == null || value.isEmpty()) ? defaultValue : value;
    }

    private static Session getSession() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, APP_PASSWORD);
            }
        });
    }

    // ✅ SEND OTP TO USER
    public static void sendOtp(String toEmail, String otp) {
        try {
            Session session = getSession();

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Your OTP Code");
            message.setText("Your OTP is: " + otp + "\nDo not share this OTP.");

            Transport.send(message);

            System.out.println("OTP Email sent successfully to " + toEmail);

        } catch (Exception ex) {
            System.out.println("OTP Email FAILED: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // ✅ EMAIL TO ADMIN
    public static void sendToAdmin(String subject, String body) {
        try {
            Session session = getSession();

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(FROM_EMAIL));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);

            System.out.println("Admin email sent successfully");

        } catch (Exception ex) {
            System.out.println("Admin email FAILED: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
