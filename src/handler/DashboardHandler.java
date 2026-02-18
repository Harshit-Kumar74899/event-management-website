package handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;

public class DashboardHandler implements HttpHandler {

    private static final String FILE_PATH = "src/web/dashboard.html";

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        File file = new File(FILE_PATH);

        if (!file.exists()) {
            System.out.println("dashboard.html NOT FOUND");
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
