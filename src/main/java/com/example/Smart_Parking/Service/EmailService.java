package com.example.Smart_Parking.Service;

import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Service
public class EmailService {

    private static final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public void sendVerificationEmail(String toEmail, String token) {
        String apiKey = System.getenv("BREVO_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            throw new RuntimeException("BREVO_API_KEY not set in environment");
        }

        String domain = System.getenv("APP_DOMAIN");
        if (domain == null || domain.isEmpty()) {
            domain = "localhost:8080";
        }

        String verifyUrl = "https://" + domain + "/verify-email-submit?email=" + urlEncode(toEmail) + "&token=" + urlEncode(token);

        String jsonBody = "{"
                + "\"sender\": { \"email\": \"noreply@smartparking.com\", \"name\": \"Smart Parking\" },"
                + "\"to\": [ { \"email\": \"" + escapeJson(toEmail) + "\" } ],"
                + "\"subject\": \"Smart Parking - Verify Your Email\","
                + "\"htmlContent\": \"<p>Your verification code is: <b>" + escapeJson(token) + "</b></p>"
                + "<p><a href=\\\"" + escapeJson(verifyUrl) + "\\\">Click to verify</a></p>\""
                + "}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.brevo.com/v3/smtp/email"))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("api-key", apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        try {
            HttpResponse<String> resp = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            int status = resp.statusCode();
            if (status >= 200 && status < 300) {
                System.out.println("Verification Email Sent via Brevo API to " + toEmail);
            } else {
                System.err.println("Brevo API returned status " + status + " body: " + resp.body());
                throw new RuntimeException("Brevo API error: " + status);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }

    // very small helpers
    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private static String urlEncode(String s) {
        try {
            return java.net.URLEncoder.encode(s, java.nio.charset.StandardCharsets.UTF_8);
        } catch (Exception e) {
            return s;
        }
    }
}
