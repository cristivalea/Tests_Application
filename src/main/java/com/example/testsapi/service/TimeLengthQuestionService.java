package com.example.testsapi.service;

import com.example.testsapi.entity.TimeLengthQuestion;
import com.example.testsapi.entity.TimeLengthQuestionId;
import com.example.testsapi.repository.TimeLengthQuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TimeLengthQuestionService {

    @Autowired
    private TimeLengthQuestionRepository repository;

    // Adăugare timp pentru întrebare
    public TimeLengthQuestion addTimeLength(TimeLengthQuestion timeLengthQuestion) {
        Integer nextId = repository.findMaxId() + 1;
        timeLengthQuestion.setId(nextId);
        return repository.save(timeLengthQuestion);
    }

    // Modificare timp pentru întrebare
    public TimeLengthQuestion updateTimeLength(Integer id, String idQuestion, Integer newTime) {
        TimeLengthQuestionId key = new TimeLengthQuestionId(id, idQuestion);
        TimeLengthQuestion existing = repository.findById(key).orElseThrow(() -> new RuntimeException("Error search register"));

        existing.setTimeLength(newTime);
        return repository.save(existing);
    }

    // Ștergere timp pentru întrebare
    public void deleteTimeLength(Integer id, String idQuestion) {
        TimeLengthQuestionId key = new TimeLengthQuestionId(id, idQuestion);
        if (!repository.existsById(key)) {
            throw new RuntimeException("Error search question");
        }
        repository.deleteById(key);
    }

    // Căutare întrebări după interval de timp
    public List<TimeLengthQuestion> searchByTimeRange(Integer minTime, Integer maxTime) {
        return repository.findByTimeLengthBetween(minTime, maxTime);
    }

    // Căutare după id întrebare
    public List<TimeLengthQuestion> getByIdQuestion(String idQuestion) {
        return repository.findByIdQuestion(idQuestion);
    }
}
