package com.example.testsapi.controller;

import com.example.testsapi.entity.QuestionCodeLink;
import com.example.testsapi.service.QuestionCodeLinKService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/question_code_link")
public class QuestionCodeLinkController {

    @Autowired
    private QuestionCodeLinKService questionCodeLinKService;

    @PostMapping("/add")
    public ResponseEntity<QuestionCodeLink> addQuestionCodeLink(@RequestBody QuestionCodeLink questionCodeLink){
        return ResponseEntity.ok(questionCodeLinKService.addQuestionCodeLink(questionCodeLink));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteQuestionCodeLink(@PathVariable int id){
        try{
            questionCodeLinKService.deleteQuestionCodeLink(id);
            return ResponseEntity.ok("Question Code link delete successfully");
        }catch (RuntimeException e){
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}
