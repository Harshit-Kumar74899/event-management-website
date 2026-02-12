package handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

public class LoginHandler implements HttpHandler {

    private static final String FILE_PATH = "src/web/login.html";

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        // Only allow GET request to open page
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
