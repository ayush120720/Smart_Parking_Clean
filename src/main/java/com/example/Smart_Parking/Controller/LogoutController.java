package com.example.Smart_Parking.Controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LogoutController {

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();  // END SESSION
        return "redirect:/Home";   // REDIRECT TO HOME PAGE
    }
}
