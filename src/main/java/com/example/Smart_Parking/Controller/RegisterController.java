package com.example.Smart_Parking.Controller;

import com.example.Smart_Parking.Model.User;
import com.example.Smart_Parking.Service.EmailService;
import com.example.Smart_Parking.Service.UserService;
import com.example.Smart_Parking.Service.VerificationService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class RegisterController {

    private final UserService userService;
    private final EmailService emailService;
    private final VerificationService verificationService;

    public RegisterController(UserService userService,
                              EmailService emailService,
                              VerificationService verificationService) {
        this.userService = userService;
        this.emailService = emailService;
        this.verificationService = verificationService;
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("userForm", new User());
        return "Register";
    }

    @PostMapping("/register")
    public String registerSubmit(@ModelAttribute("userForm") @Valid User userForm,
                                 BindingResult br,
                                 Model model) {

        // DEBUG (masked) - remove for production
        String debugUser = System.getenv("SPRING_MAIL_USERNAME");
        boolean pwLoaded = System.getenv("SPRING_MAIL_PASSWORD") != null;
        System.out.println("DEBUG SPRING_MAIL_USERNAME = " + (debugUser == null ? "NULL" : debugUser));
        System.out.println("DEBUG SPRING_MAIL_PASSWORD = " + (pwLoaded ? "LOADED" : "NULL"));

        if (br.hasErrors()) {
            return "Register";
        }

        boolean registered = userService.register(userForm);
        if (!registered) {
            model.addAttribute("error", "Email already exists.");
            return "Register";
        }

        String token = verificationService.generateToken(userForm.getEmail());

        try {
            emailService.sendVerificationEmail(userForm.getEmail(), token);
        } catch (Exception ex) {
            ex.printStackTrace(); // keep for now; you can log with logger
            model.addAttribute("error", "Failed to send verification email. Please try resending.");
            model.addAttribute("email", userForm.getEmail());
            return "VerifyEmail"; // show verify UI with resend option
        }

        model.addAttribute("email", userForm.getEmail());
        return "VerifyEmail";
    }



    @PostMapping("/verify-email-submit")
    public String verifyEmail(@RequestParam String email,
                              @RequestParam String token,
                              Model model) {

        boolean valid = verificationService.verifyToken(email, token);

        if (!valid) {
            model.addAttribute("email", email);
            model.addAttribute("error", "Invalid or expired OTP.");
            return "VerifyEmail";
        }

        verificationService.invalidate(email);
        userService.markEmailVerified(email);

        return "redirect:/Userlogin";
    }
}
