package com.example.testsapi.controller;

import com.example.testsapi.dto.EmailRequest;
import com.example.testsapi.dto.SecurityQuestionRequest;
import com.example.testsapi.entity.Email;
import com.example.testsapi.entity.Pair;
import com.example.testsapi.entity.SecurityQuestion;
import com.example.testsapi.service.SecurityQuestionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    public ResponseEntity<SecurityQuestion> addSequrityQuestion(@Valid @RequestBody SecurityQuestionRequest request) {
        SecurityQuestion secQuestSaved = securityQuestionService.addSecurityQuestion(request.getUserId(), request.getSec_quest(), request.getResponse());
        return ResponseEntity.ok(secQuestSaved);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteSequrityQuestion(@PathVariable String userId){
        securityQuestionService.deleteSequrityQuestion(userId);
        return ResponseEntity.ok("Sequrity question delete successfully!");
    }

    @PatchMapping("/{userId}")
    public void updateSecurityQuestion(@PathVariable String userId, @RequestParam String question, @RequestParam String newResponse) {
        securityQuestionService.updateResponseSecurityQuestion(userId, question, newResponse);
    }
}
