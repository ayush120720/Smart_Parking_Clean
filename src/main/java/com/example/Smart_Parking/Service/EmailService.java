package com.example.Smart_Parking.Service;

import org.springframework.stereotype.Service;
import java.net.http.*;
import java.net.URI;

@Service
public class EmailService {

    public void sendVerificationEmail(String toEmail, String token) {
        String apiKey = System.getenv("RESEND_API_KEY");

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
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("RESEND STATUS = " + response.statusCode());
            System.out.println("RESEND RESPONSE = " + response.body());
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }
}
