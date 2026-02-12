package util;

import com.sendgrid.*;
import java.io.IOException;

public class EmailUtil {

    // ❌ DO NOT HARDCODE
    // ✅ Load from Render Environment Variable
    private static final String API_KEY = System.getenv("SENDGRID_API_KEY");

    private static final String FROM_EMAIL = "harshitkumar.itm.092004@gmail.com";

    public static void sendOtp(String toEmail, String otp) {

        Email from = new Email(FROM_EMAIL);
        Email to = new Email(toEmail);

        String subject = "Your OTP Code";

        String contentText =
                "Your OTP is: " + otp +
                "\n\nDo not share this OTP.";

        Content content = new Content("text/plain", contentText);

        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(API_KEY);

        Request request = new Request();

        try {

            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);

            System.out.println("OTP Email sent. Status Code: " + response.getStatusCode());

        } catch (IOException ex) {

            System.out.println("Email sending failed");
            ex.printStackTrace();
        }
    }

    public static void sendToAdmin(String subject, String body) {

        Email from = new Email(FROM_EMAIL);
        Email to = new Email(FROM_EMAIL);

        Content content = new Content("text/plain", body);

        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(API_KEY);

        Request request = new Request();

        try {

            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            sg.api(request);

        } catch (IOException ex) {

            ex.printStackTrace();
        }
    }
}
