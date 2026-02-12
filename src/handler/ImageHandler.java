package handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ImageHandler implements HttpHandler {

    private static final String IMAGE_DIR = "src/web/images";

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String path = exchange.getRequestURI().getPath();

        // remove "/images"
        path = path.replace("/images", "");

        File file = new File(IMAGE_DIR + path);

        if (!file.exists()) {
            exchange.sendResponseHeaders(404, -1);
            return;
        }

        FileInputStream fis = new FileInputStream(file);
        byte[] data = fis.readAllBytes();
        fis.close();

        exchange.getResponseHeaders().set("Content-Type", getContentType(path));

        exchange.sendResponseHeaders(200, data.length);

        OutputStream os = exchange.getResponseBody();
        os.write(data);
        os.close();
    }

    private String getContentType(String path) {

        if (path.endsWith(".png")) return "image/png";
        if (path.endsWith(".jpg")) return "image/jpeg";
        if (path.endsWith(".jpeg")) return "image/jpeg";

        return "application/octet-stream";
    }
}
