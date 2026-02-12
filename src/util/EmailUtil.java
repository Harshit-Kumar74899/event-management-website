package util;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailUtil {

    private static final String FROM_EMAIL = "harshitkumar.itm.092004@gmail.com";
    private static final String APP_PASSWORD = "wgzqtynbybfsqyej";

    // Common session creator
    private static Session createSession() {

        Properties props = new Properties();

        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        // ✅ FIX: prevent Render timeout
        props.put("mail.smtp.connectiontimeout", "15000");
        props.put("mail.smtp.timeout", "15000");
        props.put("mail.smtp.writetimeout", "15000");

        // ✅ REQUIRED for TLS on Render
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, APP_PASSWORD);
            }
        });

        session.setDebug(true);

        return session;
    }

    // SEND OTP TO USER
    public static void sendOtp(String toEmail, String otp) throws Exception {

        Session session = createSession();

        Message message = new MimeMessage(session);

        message.setFrom(new InternetAddress(FROM_EMAIL));

        message.setRecipients(
                Message.RecipientType.TO,
                InternetAddress.parse(toEmail)
        );

        message.setSubject("Your OTP Code");

        message.setText(
                "Your OTP is: " + otp +
                "\n\nDo not share this OTP."
        );

        Transport.send(message);

        System.out.println("OTP email sent to " + toEmail);
    }

    // SEND EMAIL TO ADMIN
    public static void sendToAdmin(String subject, String body) throws Exception {

        Session session = createSession();

        Message message = new MimeMessage(session);

        message.setFrom(new InternetAddress(FROM_EMAIL));

        message.setRecipients(
                Message.RecipientType.TO,
                InternetAddress.parse(FROM_EMAIL)
        );

        message.setSubject(subject);

        message.setText(body);

        Transport.send(message);

        System.out.println("Admin email sent");
    }
}
