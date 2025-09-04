package com.example.testsapi.controller;

import com.example.testsapi.dto.EmailRequest;
import com.example.testsapi.entity.Email;
import com.example.testsapi.service.EmailService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/emails")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<String>> getEmailByUserId(@PathVariable String userId) {
        List<String> email = emailService.getEmailsByUserId(userId);
        return ResponseEntity.ok(email);
    }

    @PostMapping
    public ResponseEntity<Email> addEmail(@Valid @RequestBody EmailRequest request) {
        Email savedEmail = emailService.addEmail(request.getUserId(), request.getAddress());
        return ResponseEntity.ok(savedEmail);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteEmail(@PathVariable Long userId){
        emailService.deleteEmail(userId);
        return ResponseEntity.ok("Email delete successfully!");
    }
}
