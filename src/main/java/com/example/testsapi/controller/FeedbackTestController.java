package com.example.testsapi.controller;

import com.example.testsapi.entity.Feedback;
import com.example.testsapi.entity.FeedbackTest;
import com.example.testsapi.service.FeedbackTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/feedbackTest")
public class FeedbackTestController {

    @Autowired
    private FeedbackTestService feedbackTestService;

    @PostMapping("/addFeedbackTest")
    public ResponseEntity<FeedbackTest> addFeddback(@RequestBody FeedbackTest feedback){
        return ResponseEntity.ok(feedbackTestService.addFeedback(feedback));
    }
}
