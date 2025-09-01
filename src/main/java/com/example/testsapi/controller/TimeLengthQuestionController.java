package com.example.testsapi.controller;

import com.example.testsapi.entity.TimeLengthQuestion;
import com.example.testsapi.service.TimeLengthQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/time-length")
public class TimeLengthQuestionController {

    @Autowired
    private TimeLengthQuestionService service;

    // Adăugare timp
    @PostMapping
    public ResponseEntity<TimeLengthQuestion> addTime(@RequestBody TimeLengthQuestion timeLengthQuestion) {
        return ResponseEntity.ok(service.addTimeLength(timeLengthQuestion));
    }

    // Modificare timp
    @PutMapping("/{id}/{idQuestion}/{newTime}")
    public ResponseEntity<TimeLengthQuestion> updateTime(@PathVariable Integer id, @PathVariable String idQuestion, @PathVariable Integer newTime) {
        return ResponseEntity.ok(service.updateTimeLength(id, idQuestion, newTime));
    }

    // Ștergere timp
    @DeleteMapping("/{id}/{idQuestion}")
    public ResponseEntity<String> deleteTime(@PathVariable Integer id, @PathVariable String idQuestion) {
        service.deleteTimeLength(id, idQuestion);
        return ResponseEntity.ok("Register delete successfully");
    }

    // Căutare întrebări după interval de timp
    @GetMapping("/search")
    public ResponseEntity<List<TimeLengthQuestion>> searchByRange(@RequestParam Integer minTime, @RequestParam Integer maxTime) {
        return ResponseEntity.ok(service.searchByTimeRange(minTime, maxTime));
    }

    // Căutare întrebări după idQuestion
    @GetMapping("/by-question/{idQuestion}")
    public ResponseEntity<List<TimeLengthQuestion>> getByIdQuestion(@PathVariable String idQuestion) {
        return ResponseEntity.ok(service.getByIdQuestion(idQuestion));
    }
}

