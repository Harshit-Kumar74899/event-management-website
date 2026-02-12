package util;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailUtil {

    // ✅ ADMIN email (default)
    private static final String ADMIN_EMAIL = "harshitkumar.itm.092004@gmail.com";

    // ✅ Gmail SMTP sender (must be a real gmail)
    private static final String FROM_EMAIL = "harshitkumar.itm.092004@gmail.com";

    // ✅ Gmail App Password (NOT your normal password)
    private static final String APP_PASSWORD = "wgzqtynbybfsqyej";

    public static void sendToAdmin(String subject, String body) throws Exception {

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, APP_PASSWORD);
            }
        });

        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(FROM_EMAIL));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(ADMIN_EMAIL));
        msg.setSubject(subject);
        msg.setText(body);

        Transport.send(msg);
        System.out.println("✅ Admin email sent: " + ADMIN_EMAIL);
    }
}
