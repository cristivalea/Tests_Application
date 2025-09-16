package com.example.testsapi.service;

import com.example.testsapi.entity.Feedback;
import com.example.testsapi.repository.FeedbackRepository;
import org.springframework.stereotype.Service;

@Service
public class FeedbackServices {
    private FeedbackRepository feedbackRepository;

    public FeedbackServices(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    public Feedback addFeedback(Feedback feedback){
        if(feedback == null){
            System.err.println("The feedback is null!");
        }
        return feedbackRepository.save(feedback);
    }

    public void deleteFeedback(Integer id){
        if(feedbackRepository.existsById(id)){
            feedbackRepository.deleteById(id);
        }
        else {
            throw new RuntimeException("This feedback does  not exit!");
        }
    }
}
