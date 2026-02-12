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

            System.out.println("Login attempt: " + formData);

            // ✅ redirect to dashboard
            exchange.getResponseHeaders().add("Location", "/dashboard");
            exchange.sendResponseHeaders(302, -1);
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
