package com.example.testsapi.service;

import com.example.testsapi.entity.Email;
import com.example.testsapi.repository.EmailRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailService {

    @Autowired
    private EmailRepository emailRepository;

    public List<Email> getEmailsByUserId(String userId) {
        return emailRepository.findByUser_IdUser(userId);
    }
}

