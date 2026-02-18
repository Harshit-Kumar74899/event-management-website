package handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class LoginHandler implements HttpHandler {

    private static final String FILE_PATH = "src/web/login.html";

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        if (exchange.getRequestMethod().equalsIgnoreCase("GET")) {

            sendFile(exchange, FILE_PATH);
            return;
        }

        if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {

    String formData = new String(
            exchange.getRequestBody().readAllBytes(),
            StandardCharsets.UTF_8
    );

    String email = null;
    String password = null;

    for (String pair : formData.split("&")) {
        String[] kv = pair.split("=");
        if (kv[0].equals("email")) {
            email = java.net.URLDecoder.decode(kv[1], "UTF-8");
        }
        if (kv[0].equals("password")) {
            password = java.net.URLDecoder.decode(kv[1], "UTF-8");
        }
    }

    System.out.println("Login attempt: " + email);

    boolean success = dao.UserDAO.login(email, password);

    if (success) {

        // ✅ get user name
        String name = dao.UserDAO.getNameByEmail(email);

        // ✅ set cookies (SERVER SIDE SESSION)
        exchange.getResponseHeaders().add(
                "Set-Cookie",
                "userEmail=" + email + "; Path=/"
        );
        exchange.getResponseHeaders().add(
                "Set-Cookie",
                "userName=" + name + "; Path=/"
        );

        exchange.getResponseHeaders().add("Location", "/dashboard");
        exchange.sendResponseHeaders(302, -1);

    } else {

        // ❌ invalid login
        exchange.getResponseHeaders().add("Location", "/login?error=1");
        exchange.sendResponseHeaders(302, -1);
    }
}

    }

    private void sendFile(HttpExchange exchange, String path) throws IOException {

        File file = new File(path);

        if (!file.exists()) {
            exchange.sendResponseHeaders(404, -1);
            return;
        }

        byte[] data = new FileInputStream(file).readAllBytes();

        exchange.getResponseHeaders().set("Content-Type", "text/html");
        exchange.sendResponseHeaders(200, data.length);

        OutputStream os = exchange.getResponseBody();
        os.write(data);
        os.close();
    }
}
