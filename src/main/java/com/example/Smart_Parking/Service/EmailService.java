package com.example.Smart_Parking.Service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(String toEmail, String otp) {

            // Debug: Check if Spring Boot loaded SMTP credentials
            String u1 = System.getenv("SPRING_MAIL_USERNAME");
            String p1 = System.getenv("SPRING_MAIL_PASSWORD");

            System.out.println("DEBUG SMTP ENV USERNAME = " + u1);
            System.out.println("DEBUG SMTP ENV PASSWORD = " + (p1 != null ? "LOADED" : "NULL"));

            String s1 = System.getProperty("spring.mail.username");
            String s2 = System.getProperty("spring.mail.password");

            System.out.println("DEBUG SMTP SYS-PROP USERNAME = " + s1);
            System.out.println("DEBUG SMTP SYS-PROP PASSWORD = " + (s2 != null ? "LOADED" : "NULL"));

            // Temporary disable email sending to avoid crash
            System.out.println("SMTP test — Email sending disabled");

    }
}
