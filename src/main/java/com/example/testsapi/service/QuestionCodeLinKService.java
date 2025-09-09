package com.example.testsapi.service;

import com.example.testsapi.entity.QuestionCodeLink;
import com.example.testsapi.repository.QuestionCodeLinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuestionCodeLinKService {

    @Autowired
    private QuestionCodeLinkRepository questionCodeLinkRepository;

    public QuestionCodeLink addQuestionCodeLink(QuestionCodeLink questionCodeLink){
        return questionCodeLinkRepository.save(questionCodeLink);
    }

    public void deleteQuestionCodeLink(Integer id){
        if(questionCodeLinkRepository.existsById(id)){
            questionCodeLinkRepository.deleteById(id);
        }
        else {
            throw new RuntimeException("Error delete source code with id: " + id);
        }
    }
}
