package com.example.testsapi.controller;

import com.example.testsapi.entity.Feedback;
import com.example.testsapi.entity.Response;
import com.example.testsapi.service.FeedbackServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/feedback")
public class FeedbackController {
    @Autowired
    private FeedbackServices feedbackServices;

    @PostMapping("/addFeedback")
    public ResponseEntity<Feedback> addFeddback(@RequestBody Feedback feedback){
        return ResponseEntity.ok(feedbackServices.addFeedback(feedback));
    }

    @DeleteMapping("/deleteFeedback/{id}")
    public ResponseEntity<String> deleteFeedbcak(@PathVariable Integer id){
        feedbackServices.deleteFeedback(id);
        return ResponseEntity.ok("Feedback successfully deleted!");
    }
}
