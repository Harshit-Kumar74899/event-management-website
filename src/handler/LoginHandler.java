package handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.UserDAO;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class LoginHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        /* =======================
           GET → LOGIN PAGE
           ======================= */
        if (exchange.getRequestMethod().equalsIgnoreCase("GET")) {

            byte[] page = getClass()
                    .getResourceAsStream("/web/login.html")
                    .readAllBytes();

            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, page.length);
            exchange.getResponseBody().write(page);
            exchange.close();
            return;
        }

        /* =======================
           ONLY POST ALLOWED
           ======================= */
        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            exchange.sendResponseHeaders(405, -1);
            exchange.close();
            return;
        }

        /* =======================
           READ FORM DATA
           ======================= */
        String body = new String(
                exchange.getRequestBody().readAllBytes(),
                StandardCharsets.UTF_8
        );

        String email = null;
        String password = null;

        for (String pair : body.split("&")) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2) {
                String key = kv[0];
                String value = URLDecoder.decode(kv[1], StandardCharsets.UTF_8);

                if (key.equals("email")) email = value;
                if (key.equals("password")) password = value;
            }
        }

        System.out.println("🔐 Login Attempt → " + email);

        /* =======================
           DB LOGIN CHECK
           ======================= */
        boolean valid = UserDAO.login(email, password);

        if (valid) {

            // 🔥 FETCH USER NAME
            String name = UserDAO.getNameByEmail(email);
            if (name == null || name.trim().isEmpty()) name = "User";

            // ✅ JS safe strings (quotes, newline etc)
            String safeName = jsEscape(name);
            String safeEmail = jsEscape(email);

            // ✅ Return HTML+JS that sets storage and redirects
            String html = ""
                    + "<!DOCTYPE html>"
                    + "<html><head><meta charset='UTF-8'><title>Redirecting...</title></head><body>"
                    + "<script>"
                    + "var userNameFromLogin = \"" + safeName + "\";"
                    + "var userEmailFromLogin = \"" + safeEmail + "\";"
                    + "localStorage.setItem(\"userName\", userNameFromLogin);"
                    + "localStorage.setItem(\"userEmail\", userEmailFromLogin);"
                    + "sessionStorage.removeItem(\"welcomeShown\");"
                    + "window.location.href = \"dashboard.html\";"
                    + "</script>"
                    + "</body></html>";

            byte[] resp = html.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, resp.length);
            exchange.getResponseBody().write(resp);
            exchange.close();
            return;

        } else {
            // ❌ LOGIN FAIL
            exchange.getResponseHeaders().add("Location", "/login?error=invalid");
            exchange.sendResponseHeaders(302, -1);
            exchange.close();
        }
    }

    // ✅ Escape for JS string
    private static String jsEscape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}
