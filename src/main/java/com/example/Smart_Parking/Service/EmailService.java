package com.example.Smart_Parking.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class EmailService {

    @Value("${MAILTRAP_API_TOKEN}")
    private String apiToken;

    @Value("${MAILTRAP_SENDER_EMAIL}")
    private String senderEmail;

    public void sendVerificationEmail(String toEmail, String otp) {

        try {
            String jsonBody = """
                {
                  "from": { "email": "%s" },
                  "to": [{ "email": "%s" }],
                  "subject": "Your Smart Parking OTP",
                  "text": "Your OTP is: %s"
                }
            """.formatted(senderEmail, toEmail, otp);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://send.api.mailtrap.io/api/send"))
                    .header("Authorization", "Bearer " + apiToken)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("MAILTRAP API EMAIL SENT TO " + toEmail);

        } catch (Exception e) {
            System.out.println("MAILTRAP API ERROR = " + e.getMessage());
            throw new RuntimeException("Email sending failed", e);
        }
    }
}
