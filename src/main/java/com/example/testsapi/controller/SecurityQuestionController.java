package com.example.testsapi.controller;

import com.example.testsapi.entity.Pair;
import com.example.testsapi.entity.SecurityQuestion;
import com.example.testsapi.service.SecurityQuestionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/security_questions")
public class SecurityQuestionController {
    private final SecurityQuestionService securityQuestionService;

    public SecurityQuestionController(SecurityQuestionService securityQuestionService) {
        this.securityQuestionService = securityQuestionService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<Pair<String, String>>> getSecurityQuestionsByUserId(@PathVariable String userId) {
        List<Pair<String, String>> securityQuestions = securityQuestionService.getSecurityQuestions(userId);
        return ResponseEntity.ok(securityQuestions);
    }
}
