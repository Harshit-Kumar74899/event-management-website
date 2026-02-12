import com.sun.net.httpserver.HttpServer;
import handler.IndexHandler;
import handler.LoginHandler;
import handler.SignupHandler;
import handler.AppointmentHandler;
import handler.DashboardHandler;
import handler.ImageHandler;
import handler.VerifyOtpHandler;
import handler.StaticFileHandler;

import java.net.InetSocketAddress;

public class MainServer {

    public static void main(String[] args) {

        try {
            int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8081"));

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
