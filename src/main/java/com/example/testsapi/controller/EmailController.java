package com.example.testsapi.controller;

import com.example.testsapi.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/emails")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getEmailsForUser(@PathVariable String userId) {
        return ResponseEntity.ok(emailService.getEmailsByUserId(userId));
    }
}
