package com.example.testsapi.service;

import com.example.testsapi.repository.QuestionLabelsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionLabelsService {
    private QuestionLabelsRepository questionLabelsRepository;

    public List<String> getLabels(){
        return null;
    }
}
