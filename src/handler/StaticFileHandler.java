package handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;

public class StaticFileHandler implements HttpHandler {

    private final String root;

    public StaticFileHandler(String root) {
        this.root = root;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String path = exchange.getRequestURI().getPath();

        if (path.equals("/")) {
            path = "/index.html";
        }

        // 🔥 Load from classpath
        InputStream is =
                getClass().getResourceAsStream("/" + root + path);

        if (is == null) {
            exchange.sendResponseHeaders(404, -1);
            return;
        }

        byte[] data = is.readAllBytes();

        exchange.getResponseHeaders()
                .set("Content-Type", getContentType(path));

        exchange.sendResponseHeaders(200, data.length);
        exchange.getResponseBody().write(data);
        exchange.close();
    }

    private String getContentType(String path) {
        String lower = path.toLowerCase();
        if (lower.endsWith(".html")) return "text/html; charset=UTF-8";
        if (lower.endsWith(".css")) return "text/css; charset=UTF-8";
        if (lower.endsWith(".js")) return "application/javascript; charset=UTF-8";
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
        return "application/octet-stream";
    }
}
