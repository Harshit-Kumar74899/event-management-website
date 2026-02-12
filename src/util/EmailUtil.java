package util;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailUtil {

    private static final String FROM_EMAIL = "harshitkumar.itm.092004@gmail.com";

    private static final String APP_PASSWORD = "wgzqtynbybfsqyej";

    // ✅ SEND OTP TO USER
    public static void sendOtp(String toEmail, String otp) throws Exception {

        Properties props = new Properties();

        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {

            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, APP_PASSWORD);
            }

        });

        Message message = new MimeMessage(session);

        message.setFrom(new InternetAddress(FROM_EMAIL));

        message.setRecipients(
                Message.RecipientType.TO,
                InternetAddress.parse(toEmail)
        );

        message.setSubject("Your OTP Code");

        message.setText(
                "Your OTP is: " + otp +
                "\n\nDo not share this OTP with anyone."
        );

        Transport.send(message);

        System.out.println("OTP sent to " + toEmail);
    }
}
