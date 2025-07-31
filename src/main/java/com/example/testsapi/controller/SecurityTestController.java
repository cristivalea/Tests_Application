package com.example.testsapi.controller;

import com.example.testsapi.entity.User;
import com.example.testsapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/test")
public class SecurityTestController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/create-user-with-password")
    public String createUserWithHashedPassword() {
        try {
            Integer maxId = userRepository.findMaxUserIdNumber();
            String newId = String.format("U%05d", (maxId != null ? maxId + 1 : 1));

            User testUser = new User(newId, "Secure", "User", "secureuser",
                    passwordEncoder.encode("password123"));
            testUser.setDataBirth(LocalDate.of(1990, 1, 1));
            testUser.setPhoneNumber("+40123456789");

            User savedUser = userRepository.save(testUser);

            return "✅ Utilizator securizat creat cu succes!\n" +
                    "ID: " + savedUser.getIdUser() + "\n" +
                    "Username: " + savedUser.getUsername() + "\n" +
                    "Password (hash): " + savedUser.getPassword().substring(0, 20) + "...";

        } catch (Exception e) {
            return "❌ Eroare la crearea utilizatorului: " + e.getMessage();
        }
    }

    @GetMapping("/who-am-i")
    public String whoAmI() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() &&
                !authentication.getName().equals("anonymousUser")) {

            String username = authentication.getName();
            String authorities = authentication.getAuthorities().toString();

            return "✅ Utilizator autentificat cu JWT!\n" +
                    "Username: " + username + "\n" +
                    "Authorities: " + authorities;
        } else {
            return "❌ Niciun utilizator autentificat sau token invalid";
        }
    }

    @GetMapping("/secure-endpoint")
    public String secureEndpoint() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getPrincipal() instanceof User) {
            User currentUser = (User) authentication.getPrincipal();

            return "🔐 Acces autorizat cu JWT!\n" +
                    "Bună ziua, " + currentUser.getFullName() + "!\n" +
                    "ID: " + currentUser.getIdUser() + "\n" +
                    "Email: " + currentUser.getPrimaryEmail();
        } else {
            return "❌ Token invalid sau utilizator neautentificat";
        }
    }

    @GetMapping("/user-profile")
    public User getUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }
}