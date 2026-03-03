package com.example.testsapi.service;

import com.example.testsapi.entity.Session;
import com.example.testsapi.repository.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SessionService {
    private final SessionRepository sessionRepository;

    public SessionService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public Session saveSession(String token, String userId, int minutesValid) {
        Session session = new Session();
        session.setToken(token);
        session.setUserId(userId);
        session.setStartSession(LocalDateTime.now());
        session.setEndSession(LocalDateTime.now().plusMinutes(minutesValid));

        return sessionRepository.save(session);
    }

    public boolean isValid(String token) {
        // findByToken returnează Optional<Session>, deci folosim .map()
        return sessionRepository.findByToken(token)
                .map(session -> session.getEndSession().isAfter(LocalDateTime.now()))
                .orElse(false); // Dacă nu găsește sesiunea, returnează false
    }

    public void deleteSession(String token) {
        // Căutăm sesiunea și, dacă există, o ștergem
        sessionRepository.findByToken(token).ifPresent(session -> {
            sessionRepository.delete(session);
        });
    }
}
