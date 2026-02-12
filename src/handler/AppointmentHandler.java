package handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.AppointmentDAO;
import util.EmailUtil;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class AppointmentHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        /* =======================
           ✅ GET → APPOINTMENT PAGE
           ======================= */
        if (exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            byte[] page = getClass()
                    .getResourceAsStream("/web/appointment.html")
                    .readAllBytes();

            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, page.length);
            exchange.getResponseBody().write(page);
            exchange.close();
            return;
        }

        /* =======================
           ✅ ONLY POST ALLOWED AFTER THIS
           ======================= */
        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            exchange.sendResponseHeaders(405, -1);
            exchange.close();
            return;
        }

        // Read JSON
        String json = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

        // Parse JSON (simple)
        String name = getJsonValue(json, "name");
        String mobile = getJsonValue(json, "mobile");
        String service = getJsonValue(json, "service");
        String otherService = getJsonValue(json, "otherService");
        String date = getJsonValue(json, "date");
        String time = getJsonValue(json, "time");
        String message = getJsonValue(json, "message");

        if (name == null || mobile == null || service == null || date == null || time == null) {
            sendJson(exchange, 400, "{\"ok\":false,\"error\":\"Missing required fields\"}");
            return;
        }

        // Save in DB
        int newId = AppointmentDAO.insert(name, mobile, service, otherService, date, time, message);

        // Email admin (best effort)
        try {
            String subject = "New Appointment Request (ID: " + newId + ")";
            String body =
                    "New appointment received:\n\n" +
                    "ID: " + newId + "\n" +
                    "Name: " + name + "\n" +
                    "Mobile: " + mobile + "\n" +
                    "Service: " + service +
                    (("Other".equalsIgnoreCase(service) && otherService != null && !otherService.isBlank())
                            ? (" (" + otherService + ")") : "") + "\n" +
                    "Preferred Date: " + date + "\n" +
                    "Preferred Time: " + time + "\n" +
                    "Message: " + (message == null ? "" : message) + "\n\n" +
                    "— Shubh Mangalamh Events System";

            EmailUtil.sendToAdmin(subject, body);

        } catch (Exception e) {
            // email fail hua to bhi DB me entry ho chuki hai
            e.printStackTrace();
        }

        sendJson(exchange, 200, "{\"ok\":true,\"id\":" + newId + "}");
    }

    private static void sendJson(HttpExchange exchange, int status, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(status, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.close();
    }

    // VERY small JSON getter (works for your simple payload)
    private static String getJsonValue(String json, String key) {
        if (json == null) return null;
        String pattern = "\"" + key + "\"";
        int i = json.indexOf(pattern);
        if (i < 0) return null;
        int colon = json.indexOf(":", i);
        if (colon < 0) return null;

        int start = colon + 1;
        while (start < json.length() && Character.isWhitespace(json.charAt(start))) start++;

        // value can be "string" or null/true/false
        if (start < json.length() && json.charAt(start) == '"') {
            start++;
            int end = json.indexOf('"', start);
            if (end < 0) return null;
            return json.substring(start, end);
        } else {
            // read until comma or }
            int end = start;
            while (end < json.length() && json.charAt(end) != ',' && json.charAt(end) != '}') end++;
            String raw = json.substring(start, end).trim();
            if ("null".equals(raw)) return null;
            return raw;
        }
    }
}
