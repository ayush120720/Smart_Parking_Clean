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

    public void sendVerificationEmail(String toEmail, String token) {

        String apiKey = System.getenv("RESEND_API_KEY");
        if (apiKey == null) apiKey = System.getenv("JAVA_RESEND_API_KEY");

        if (apiKey == null) throw new RuntimeException("Resend API key not found!");

        String body = """
        {
          "from": "Smart Parking <onboarding@resend.dev>",
          "to": ["%s"],
          "subject": "Your Verification Code",
          "html": "<h2>Your OTP is: %s</h2><p>Use this code to verify your email within 10 minutes.</p>"
        }
        """.formatted(toEmail, token);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.resend.com/emails"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        try {
            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("RESEND STATUS = " + response.statusCode());
            System.out.println("RESEND RESPONSE = " + response.body());

        } catch (Exception e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }
}
