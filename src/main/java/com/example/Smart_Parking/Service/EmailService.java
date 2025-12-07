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
        System.out.println("SMTP test — Email sending disabled");
    }


  /*  public void sendVerificationEmail(String toEmail, String otp) {

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(toEmail);
        msg.setSubject("Smart Parking Verification Code");
        msg.setText("Your OTP is: " + otp + "\n\nThis code is valid for 10 minutes.");

        mailSender.send(msg);

        System.out.println("SMTP EMAIL SENT TO: " + toEmail);
    }*/
}
