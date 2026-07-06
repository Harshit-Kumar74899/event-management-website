import com.sun.net.httpserver.HttpServer;
import handler.IndexHandler;
import handler.LoginHandler;
import handler.ResetPasswordHandler;
import handler.SignupHandler;
import handler.AppointmentHandler;
import handler.CheckEmailHandler;
import handler.DashboardHandler;
import handler.ImageHandler;
import handler.VerifyOtpHandler;
import handler.StaticFileHandler;

import java.net.InetSocketAddress;

public class MainServer {

    public static void main(String[] args) {

        try {
            // 🔹 Render apna PORT environment variable deta hai, usi pe listen karna zaroori hai.
            String portEnv = System.getenv("PORT");
            int port = (portEnv != null && !portEnv.isEmpty()) ? Integer.parseInt(portEnv) : 8081;

            HttpServer server = HttpServer.create(
                    new InetSocketAddress(port), 0
            );

            // 🔹 STATIC FILE HANDLER (HTML, CSS, JS)
            server.createContext("/", new StaticFileHandler("web"));

            // 🔹 DYNAMIC PAGES / APIs (more specific paths win)
            server.createContext("/login", new LoginHandler());
            server.createContext("/signup", new SignupHandler());
            server.createContext("/dashboard", new DashboardHandler());
            server.createContext("/verify-otp", new VerifyOtpHandler());
            server.createContext("/appointment", new AppointmentHandler());
            server.createContext("/check-email", new CheckEmailHandler());
            server.createContext("/reset-password", new ResetPasswordHandler());



            // 🔹 STATIC IMAGES
            server.createContext("/images", new ImageHandler());

            server.setExecutor(null);
            server.start();

            System.out.println("==================================");
            System.out.println(" Server running at http://localhost:" + port);
            System.out.println("==================================");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
