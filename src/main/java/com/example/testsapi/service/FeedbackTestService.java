package com.example.testsapi.service;

import com.example.testsapi.entity.Feedback;
import com.example.testsapi.entity.FeedbackTest;
import com.example.testsapi.repository.FeedbackTestRepository;
import org.springframework.stereotype.Service;

@Service
public class FeedbackTestService {
    private FeedbackTestRepository feedbackTestRepository;

    public FeedbackTestService(FeedbackTestRepository feedbackTestRepository) {
        this.feedbackTestRepository = feedbackTestRepository;
    }

    public FeedbackTest addFeedback(FeedbackTest feedbackTest){
        if(feedbackTest == null){
            System.err.println("The feedback is null!");
        }
        return feedbackTestRepository.save(feedbackTest);
    }
}
