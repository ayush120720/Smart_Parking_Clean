package com.example.Smart_Parking.Controller;

import com.example.Smart_Parking.Model.User;
import com.example.Smart_Parking.Service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class LoginController {

    private final UserService svc;

    public LoginController(UserService svc) {
        this.svc = svc;
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/Home";
    }

    @GetMapping("/Userlogin")
    public String loginPage() {
        return "Userlogin";   // Your login page name
    }

    @PostMapping("/Userlogin")
    public String loginSubmit(@RequestParam String username,
                              @RequestParam String password,
                              HttpSession session) {

        Optional<User> maybeUser = svc.authenticateAndGetUser(username, password);

        if (maybeUser.isPresent()) {
            User user = maybeUser.get();

            // ★ IMPORTANT: store user in session
            session.setAttribute("loggedUser", user);

            return "redirect:/Home";
        }

        return "redirect:/loginError";
    }

    @GetMapping("/loginError")
    public String loginErrorPage() {
        return "loginError";
    }
}
