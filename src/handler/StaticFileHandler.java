package handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;

public class StaticFileHandler implements HttpHandler {

    private final File rootDir;

    public StaticFileHandler(String root) {

        // 🔥 Important fix for Render
        this.rootDir = new File(System.getProperty("user.dir"), root);

        System.out.println("Static root: " + rootDir.getAbsolutePath());
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String path = exchange.getRequestURI().getPath();

        if (path.equals("/")) {
            path = "/index.html";
        }

        File file = new File(rootDir, path);

        System.out.println("Requested file: " + file.getAbsolutePath());

        if (!file.exists() || file.isDirectory()) {
            System.out.println("File not found");
            exchange.sendResponseHeaders(404, -1);
            return;
        }

        byte[] data = new FileInputStream(file).readAllBytes();

        exchange.getResponseHeaders()
                .set("Content-Type", getContentType(path));

        exchange.sendResponseHeaders(200, data.length);

        OutputStream os = exchange.getResponseBody();
        os.write(data);
        os.close();
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
