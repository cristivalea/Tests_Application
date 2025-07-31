package com.example.testsapi.controller;

import com.example.testsapi.repository.EmailRepository;
import com.example.testsapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/stats")
public class StatsController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailRepository emailRepository;

    @GetMapping("/dashboard")
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        // Statistici de bază
        stats.put("totalUsers", userRepository.count());
        stats.put("totalEmails", emailRepository.count());

        // Ultima înregistrare
        var lastUsers = userRepository.findAll(
                org.springframework.data.domain.PageRequest.of(0, 1,
                        org.springframework.data.domain.Sort.by("registrationDate").descending())
        );

        if (!lastUsers.isEmpty()) {
            var lastUser = lastUsers.getContent().get(0);
            stats.put("lastRegistration", Map.of(
                    "username", lastUser.getUsername(),
                    "date", lastUser.getRegistrationDate(),
                    "name", lastUser.getFullName()
            ));
        }

        // Status API
        stats.put("apiStatus", "operational");
        stats.put("timestamp", java.time.LocalDateTime.now());

        return stats;
    }
}