package com.example.testsapi.service;

import com.example.testsapi.entity.Responses;
import com.example.testsapi.repository.ResponsesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ResponsesService {
    @Autowired
    private ResponsesRepository responsesRepository;

    public Responses addReponse(String idQuestion, Integer idResponse) {
        Responses r = new Responses();
        r.setIdQuestion(idQuestion);
        r.setIdResponse(idResponse);
        return responsesRepository.save(r);
    }

    public void deleteResponses(Integer idResponse){
        if(responsesRepository.existsById(idResponse)){
            responsesRepository.deleteById(idResponse);
        }
        else {
            throw new RuntimeException("Error deleted responses");
        }
    }

    public Responses getResponses(Integer id){
        Optional<Responses> responses = responsesRepository.findById(id);
        return responses.orElse(null);
    }
}
