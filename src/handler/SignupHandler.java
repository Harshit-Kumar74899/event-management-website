package handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SignupHandler implements HttpHandler {

    public static Map<String, String> otpStore = new HashMap<>();
    public static Map<String, TempUser> tempUserStore = new HashMap<>();

    public static class TempUser {
        public String name;
        public String email;
        public String password;

        public TempUser(String name, String email, String password) {
            this.name = name;
            this.email = email;
            this.password = password;
        }
    }

    private static final String FILE_PATH = "src/web/signup.html";

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        if (exchange.getRequestMethod().equalsIgnoreCase("GET")) {

            sendFile(exchange, FILE_PATH);
            return;
        }

        if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {

            String formData = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

            Map<String, String> data = parseFormData(formData);

            String name = data.get("name");
            String email = data.get("email");
            String password = data.get("password");

            // generate OTP
            String otp = String.valueOf(100000 + new Random().nextInt(900000));

            otpStore.put(email, otp);
            tempUserStore.put(email, new TempUser(name, email, password));

            System.out.println("OTP for " + email + " is: " + otp);

            // TODO: send email here using EmailUtil
            try {
                util.EmailUtil.sendOtp(email, otp);
            } catch (Exception e) {
                e.printStackTrace();
            }


            String response = "otp_sent";

            exchange.sendResponseHeaders(200, response.length());

            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    private void sendFile(HttpExchange exchange, String path) throws IOException {

        File file = new File(path);

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

    private Map<String, String> parseFormData(String formData) throws UnsupportedEncodingException {

        Map<String, String> map = new HashMap<>();

        String[] pairs = formData.split("&");

        for (String pair : pairs) {

            String[] keyValue = pair.split("=");

            String key = URLDecoder.decode(keyValue[0], "UTF-8");

            String value = keyValue.length > 1
                    ? URLDecoder.decode(keyValue[1], "UTF-8")
                    : "";

            map.put(key, value);
        }

        return map;
    }
}
