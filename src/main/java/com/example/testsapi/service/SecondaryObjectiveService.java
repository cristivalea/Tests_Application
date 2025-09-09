package com.example.testsapi.service;

import com.example.testsapi.entity.SecondaryObjective;
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

    public SecondaryObjective updateObjective(Integer id, SecondaryObjective newData) {
        SecondaryObjective existing = secondaryObjectiveRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Obiectivul nu a fost găsit!"));

        existing.setIdObjectiv(newData.getIdObjectiv());
        existing.setNameObjectiv(newData.getNameObjectiv());

        return secondaryObjectiveRepository.save(existing);
    }

    public void deleteObjective(Integer id) {
        if (!secondaryObjectiveRepository.existsById(id)) {
            throw new RuntimeException("Obiectivul nu a fost găsit!");
        }
        secondaryObjectiveRepository.deleteById(id);
    }
}
