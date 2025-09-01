package com.example.testsapi.service;

import com.example.testsapi.entity.MainObjective;
import com.example.testsapi.entity.MainObjectiveId;
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

    public void deleteObjective(Integer id, String idQuestion) {
        MainObjectiveId key = new MainObjectiveId(id, idQuestion);

        if (!repository.existsById(key)) {
            throw new RuntimeException("Error search objective");
        }

        repository.deleteById(key);
    }

    public MainObjective updateObjective(Integer id, String idQuestion, MainObjective newData) {
        MainObjectiveId obj = new MainObjectiveId(id, idQuestion);
        MainObjective existingObjective = repository.findById(obj).orElseThrow(() -> new RuntimeException("Obiectivul nu a fost găsit!"));
        existingObjective.setIdObjectiv(newData.getIdObjectiv());
        existingObjective.setNameObjectiv(newData.getNameObjectiv());
        return repository.save(existingObjective);
    }
}