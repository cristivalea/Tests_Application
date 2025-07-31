package com.example.testsapi.service;

import com.example.testsapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserIdGeneratorService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public String generateNextUserId() {
        Integer maxNumber = userRepository.findMaxUserIdNumber();
        int nextNumber = (maxNumber != null) ? maxNumber + 1 : 1;

        return String.format("U%05d", nextNumber);
    }
}