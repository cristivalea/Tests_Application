package com.example.testsapi.service;

import com.example.testsapi.entity.Response;
import com.example.testsapi.repository.ResponseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ResponseService {
    @Autowired
    private ResponseRepository repository;

    public Response addResponse(Response response){
        return repository.save(response);
    }

    public void deleteResponse(Integer id){
        if(repository.existsById(id)){
            repository.deleteById(id);
        } else {
            throw new RuntimeException("Response with ID " + id + " not found");
        }
    }

    public Response displayResponse(Integer id){
        Optional<Response> response = repository.findById(id);
        return response.orElse(null);
    }
}
