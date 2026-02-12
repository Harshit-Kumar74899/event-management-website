package handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class SignupHandler implements HttpHandler {

    // ✅ ADD THESE VARIABLES (VERY IMPORTANT)
    public static Map<String, String> otpStore = new HashMap<>();

    public static Map<String, TempUser> tempUserStore = new HashMap<>();

    // ✅ TempUser class (used by VerifyOtpHandler)
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

    private static final String FILE_PATH = "src/web/signup.html";

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        // Only allow GET request to show signup page
        if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        File file = new File(FILE_PATH);

        if (!file.exists()) {
            exchange.sendResponseHeaders(404, -1);
            return;
        }

        FileInputStream fis = new FileInputStream(file);
        byte[] data = fis.readAllBytes();
        fis.close();

        exchange.getResponseHeaders().set("Content-Type", "text/html");

        exchange.sendResponseHeaders(200, data.length);

        OutputStream os = exchange.getResponseBody();
        os.write(data);
        os.close();
    }
}
