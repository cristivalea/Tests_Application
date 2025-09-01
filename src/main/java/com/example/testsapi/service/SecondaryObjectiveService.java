package com.example.testsapi.service;

import com.example.testsapi.entity.SecondaryObjective;
import com.example.testsapi.entity.SecondaryObjectiveId;
import com.example.testsapi.repository.SecondaryObjectiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SecondaryObjectiveService {
    @Autowired
    private SecondaryObjectiveRepository secondaryObjectiveRepository;

    public SecondaryObjective addObjective(SecondaryObjective objective) {
        Integer nextId = secondaryObjectiveRepository.findMaxId() + 1;
        objective.setId(nextId);
        return secondaryObjectiveRepository.save(objective);
    }

    public SecondaryObjective updateObjective(Integer id, String idQuestion, SecondaryObjective newData) {
        SecondaryObjectiveId key = new SecondaryObjectiveId(id, idQuestion);

        SecondaryObjective existing = secondaryObjectiveRepository.findById(key).orElseThrow(() -> new RuntimeException("Error search secondary objective"));

        existing.setIdObjectiv(newData.getIdObjectiv());
        existing.setNameObjectiv(newData.getNameObjectiv());

        return secondaryObjectiveRepository.save(existing);
    }

    public void deleteObjective(Integer id, String idQuestion) {
        SecondaryObjectiveId key = new SecondaryObjectiveId(id, idQuestion);
        if (!secondaryObjectiveRepository.existsById(key)) {
            throw new RuntimeException("Obiectivul secundar nu există!");
        }
        secondaryObjectiveRepository.deleteById(key);
    }
}
