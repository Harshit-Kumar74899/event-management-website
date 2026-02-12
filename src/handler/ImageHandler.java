package handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;

public class ImageHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String path = exchange.getRequestURI().getPath();
        // example: /images/b1.jpeg

        // 🔥 Load from classpath (/web/images/...)
        InputStream is =
                getClass().getResourceAsStream("/web" + path);

        if (is == null) {
            exchange.sendResponseHeaders(404, -1);
            return;
        }

        String contentType = getMimeType(path);
        exchange.getResponseHeaders()
                .add("Content-Type", contentType);

        byte[] data = is.readAllBytes();
        exchange.sendResponseHeaders(200, data.length);
        exchange.getResponseBody().write(data);
        exchange.close();
    }

    private String getMimeType(String path) {
        if (path.endsWith(".png")) return "image/png";
        if (path.endsWith(".jpg") || path.endsWith(".jpeg")) return "image/jpeg";
        if (path.endsWith(".gif")) return "image/gif";
        if (path.endsWith(".webp")) return "image/webp";
        return "application/octet-stream";
    }
}
