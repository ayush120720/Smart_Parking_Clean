package com.example.Smart_Parking.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Service
public class EmailService {

    @Value("${RESEND_API_KEY:}")
    private String apiKey;

    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public void sendVerificationEmail(String toEmail, String token) {

        apiKey = apiKey.trim(); // IMPORTANT
        if (apiKey.isEmpty()) {
            throw new RuntimeException("RESEND_API_KEY not set on Railway");
        }

        String body = """
        {
          "from": "Smart Parking <onboarding@resend.dev>",
          "to": ["%s"],
          "subject": "Your Verification Code",
          "html": "<h2>Your OTP is: %s</h2><p>Valid for 10 minutes.</p>"
        }
        """.formatted(toEmail, token);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.resend.com/emails"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .timeout(Duration.ofSeconds(10))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        try {
            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("RESEND STATUS = " + response.statusCode());
            System.out.println("RESEND RESPONSE = " + response.body());

            if (response.statusCode() != 202) {
                throw new RuntimeException("Email failed: " + response.body());
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }
}
