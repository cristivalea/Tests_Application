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
        session.setToke(token);
        session.setUserId(userId);
        session.setStartSession(LocalDateTime.now());
        session.setEndSession(LocalDateTime.now().plusMinutes(minutesValid));

        return sessionRepository.save(session);
    }

    public boolean isValid(String token) {
        Session session = sessionRepository.findByToke(token);
        return session != null && session.getEndSession().isAfter(LocalDateTime.now());
    }

    public void deleteSession(String token) {
        Session session = sessionRepository.findByToke(token);
        if (session != null) {
            sessionRepository.delete(session);
        }
    }

}
