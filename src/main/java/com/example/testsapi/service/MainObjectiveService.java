package com.example.testsapi.service;

import com.example.testsapi.entity.MainObjective;
import com.example.testsapi.repository.MainObjectiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MainObjectiveService {

    @Autowired
    private MainObjectiveRepository repository;

    public MainObjective addObjective(MainObjective objective) {
        Integer nextId = repository.findMaxId() + 1;
        objective.setId(nextId);

        return repository.save(objective);
    }
}