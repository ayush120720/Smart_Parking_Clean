package com.example.Smart_Parking.Controller;

import com.example.Smart_Parking.Model.User;
import com.example.Smart_Parking.Service.UserService;
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

        if (br.hasErrors()) {
            return "Register";
        }

        boolean registered = userService.register(userForm);

        if (!registered) {
            model.addAttribute("error", "Email already exists.");
            return "Register";
        }

        // Mark user as verified automatically
        userService.markEmailVerified(userForm.getEmail());

        // Directly redirect to login
        return "redirect:/Userlogin";
    }
}
