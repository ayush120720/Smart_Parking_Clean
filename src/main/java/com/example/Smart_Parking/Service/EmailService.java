package com.example.Smart_Parking.Service;

import com.sendinblue.*;
import sibApi.TransactionalEmailsApi;
import sibModel.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class EmailService {

    public void sendVerificationEmail(String toEmail, String token) {

        try {
            String domain = System.getenv("APP_DOMAIN");
            String verifyUrl = "https://" + domain + "/verify-email-submit?email="
                    + toEmail + "&token=" + token;

            ApiClient client = new ApiClient();
            client.setApiKey(System.getenv("BREVO_API_KEY"));
            TransactionalEmailsApi api = new TransactionalEmailsApi(client);

            SendSmtpEmail email = new SendSmtpEmail()
                    .subject("Smart Parking - Verify Email")
                    .sender(new SendSmtpEmailSender().email("noreply@smartparking.com"))
                    .to(Collections.singletonList(new SendSmtpEmailTo().email(toEmail)))
                    .htmlContent("<p>Your verification code is: <b>" + token + "</b></p>"
                            + "<p>Click below to verify:</p>"
                            + "<a href='" + verifyUrl + "'>Verify Email</a>");

            api.sendTransacEmail(email);

            System.out.println("Verification Email Sent via Brevo!");

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Email sending failed: " + e.getMessage());
        }
    }
}
