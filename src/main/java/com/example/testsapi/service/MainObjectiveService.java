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

    public void deleteObjective(Integer id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Obiectivul nu a fost găsit!");
        }
        repository.deleteById(id);
    }

    public MainObjective updateObjective(Integer id, MainObjective newData) {
        MainObjective existingObjective = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Obiectivul nu a fost găsit!"));

        existingObjective.setIdObjectiv(newData.getIdObjectiv());
        existingObjective.setNameObjectiv(newData.getNameObjectiv());
        return repository.save(existingObjective);
    }

}