package com.example.testsapi.service;

import com.example.testsapi.dto.QuestionPayload;
import com.example.testsapi.entity.Question;
import com.example.testsapi.repository.QuestionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuestionService {
    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    public Question addQuestion(QuestionPayload payload) {
        try{
            Question question = new Question();
            question.setIdQuestion(payload.getId());
            String json = objectMapper.writeValueAsString(payload);
            question.setQuestion(json);
            return questionRepository.save(question);
        }catch(Exception e){
            System.err.println(e.getMessage());
        }
        return null;
    }
}
