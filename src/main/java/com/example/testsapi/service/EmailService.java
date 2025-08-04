package com.example.testsapi.service;

import com.example.testsapi.entity.Email;
import com.example.testsapi.entity.User;
import com.example.testsapi.repository.EmailRepository;

import com.example.testsapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmailService {

    private final EmailRepository emailRepository;
    private final UserRepository userRepository;

    public EmailService(EmailRepository emailRepository, UserRepository userRepository) {
        this.emailRepository = emailRepository;
        this.userRepository = userRepository;
    }

    public String getEmailByUserId(String userId) {
        Email email = emailRepository.findByUserId(userId);
        if (email == null) {
            throw new RuntimeException("Email not found for userId: " + userId);
        }
        return email.getEmail();
    }

    public Email addEmail(String userId, String email) {
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("UserId must not be null or empty");
        }

        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            throw new RuntimeException("User not found with id: " + userId);
        }

        Email email1 = new Email();
        email1.setEmail(email);
        email1.setUser(userOpt.get());
        return emailRepository.save(email1);
    }

    public void deleteEmail(String userId){
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("UserId must not be null or empty");
        }

        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            throw new RuntimeException("User not found with id: " + userId);
        }
        Email email = emailRepository.findByUserId(userId);
        if(email == null){
            System.err.println("Error search email - delete");
        }
        emailRepository.delete(email);
    }
}

