package handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;

public class IndexHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        // ✅ Sirf GET request allow
        if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        InputStream is = getClass().getResourceAsStream("/web/index.html");

        if (is == null) {
            String response = "index.html not found";
            exchange.sendResponseHeaders(404, response.length());
            exchange.getResponseBody().write(response.getBytes());
            exchange.close();
            return;
        }

        byte[] page = is.readAllBytes();

        exchange.sendResponseHeaders(200, page.length);
        exchange.getResponseBody().write(page);
        exchange.close();
        is.close();
    }
}
