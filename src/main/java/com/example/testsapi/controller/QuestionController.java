package com.example.testsapi.controller;

import com.example.testsapi.entity.Question;
import com.example.testsapi.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @PostMapping
    public ResponseEntity<?> addQuestion(@RequestBody String json) {
        try {
            Question saved = questionService.saveQuestion(json);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Eroare: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/{idQuestion}")
    public ResponseEntity<?> getFormattedQuestion(
            @PathVariable Integer id,
            @PathVariable String idQuestion) {
        try {
            String formattedJson = questionService.getFormattedQuestion(id, idQuestion);
            return ResponseEntity.ok(formattedJson);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Eroare: " + e.getMessage());
        }
    }
}