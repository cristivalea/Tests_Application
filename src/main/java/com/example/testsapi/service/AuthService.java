package com.example.testsapi.service;

import com.example.testsapi.repository.SessionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {

    @Autowired
    private SessionRepository sessionRepository;

    @Transactional
    public void logout(String authHeader) {
        String token = extractToken(authHeader);

        sessionRepository.findByToken(token).ifPresent(session -> {
            // "Închidem" sesiunea setând sfârșitul ei la momentul prezent
            session.setEndSession(LocalDateTime.now());
            sessionRepository.save(session);
        });

        SecurityContextHolder.clearContext(); // Curățăm contextul de securitate
    }

    public boolean isSessionValid(String token) {
        return sessionRepository.findByToken(token)
                .map(session -> session.getEndSession().isAfter(LocalDateTime.now()))
                .orElse(false); // Dacă nu există sau end_session e în trecut, e invalidă
    }

    private String extractToken(String header) {
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return header;
    }
}