package util;

import com.sendgrid.SendGrid;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.Method;

import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Content;

import java.io.IOException;

public class EmailUtil {

    private static final String API_KEY = System.getenv("SENDGRID_API_KEY");

    private static final String FROM_EMAIL = "harshitkumar.itm.092004@gmail.com";

    // ✅ SEND OTP TO USER
    public static void sendOtp(String toEmail, String otp) {

        Email from = new Email(FROM_EMAIL, "Royal Wedding Decor");
        Email to = new Email(toEmail);

        String subject = "Your OTP Code";

        Content content = new Content(
        "text/html",
        "<div style='font-family:Arial,sans-serif; padding:20px;'>"
      + "<h2 style='color:#2c3e50;'>Royal Wedding Decor</h2>"
      + "<p>Hello,</p>"
      + "<p>Your OTP for <b>Royal Wedding Decor</b> is:</p>"
      + "<h1 style='color:#e74c3c; letter-spacing:5px;'>" + otp + "</h1>"
      + "<p>Please do not share this OTP with anyone.</p>"
      + "<br>"
      + "<p style='color:gray;'>— Royal Wedding Decor Team</p>"
      + "</div>"
);


        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(API_KEY);

        Request request = new Request();

        try {

            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);

            System.out.println("OTP Email sent: " + response.getStatusCode());

        } catch (IOException ex) {

            ex.printStackTrace();

        }
    }


    // ✅ ADD THIS METHOD (FIXES YOUR ERROR)
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

            Response response = sg.api(request);

            System.out.println("Admin email sent: " + response.getStatusCode());

        } catch (IOException ex) {

            ex.printStackTrace();

        }
    }
}
