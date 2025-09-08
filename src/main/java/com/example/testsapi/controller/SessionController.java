package com.example.testsapi.controller;

import com.example.testsapi.entity.Session;
import com.example.testsapi.repository.UserRepository;
import com.example.testsapi.service.JwtUtils;
import com.example.testsapi.service.SessionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/session")
public class SessionController {

    private final SessionService sessionService;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;

    public SessionController(SessionService sessionService, JwtUtils jwtUtils, UserRepository userRepository) {
        this.sessionService = sessionService;
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
    }


    @PostMapping("/validate")
    public ResponseEntity<?> validateAndSaveSession(@RequestBody Map<String, String> body) {
        String token = body.get("token");

        if (!jwtUtils.validateJwtToken(token)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid token"));
        }

        String username = jwtUtils.getUserNameFromJwtToken(token);


        String userId = userRepository.findByUsername(username)
                .map(u -> u.getIdUser())
                .orElse(null);

        if (userId == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "User not found"));
        }

        Session session = sessionService.saveSession(token, userId, 60);

        return ResponseEntity.ok(Map.of(
                "message", "Session started",
                "sessionId", session.getIdSourceCode(),
                "userId", userId
        ));
    }
}
