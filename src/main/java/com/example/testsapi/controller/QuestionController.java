package com.example.testsapi.controller;

import com.example.testsapi.dto.QuestionPayload;
import com.example.testsapi.entity.Question;
import com.example.testsapi.service.QuestionService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @PostMapping
    public Question addQuestion(@RequestBody QuestionPayload payload) throws Exception {
        return questionService.addQuestion(payload);
    }
}
