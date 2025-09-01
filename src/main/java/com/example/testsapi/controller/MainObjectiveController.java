package com.example.testsapi.controller;

import com.example.testsapi.entity.MainObjective;
import com.example.testsapi.service.MainObjectiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/objectives")
public class MainObjectiveController {

    @Autowired
    private MainObjectiveService service;

    @PostMapping
    public ResponseEntity<MainObjective> createObjective(@RequestBody MainObjective objective) {
        MainObjective saved = service.addObjective(objective);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}/{idQuestion}")
    public ResponseEntity<String> deleteObjective(
            @PathVariable Integer id,
            @PathVariable String idQuestion) {

        try {
            service.deleteObjective(id, idQuestion);
            return ResponseEntity.ok("Objective delete successfully!");
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PutMapping("/{id}/{idQuestion}")
    public ResponseEntity<?> updateObjective(@PathVariable Integer id, @PathVariable String idQuestion, @RequestBody MainObjective updatedObjective) {
        try {
            MainObjective updated = service.updateObjective(id, idQuestion, updatedObjective);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}
