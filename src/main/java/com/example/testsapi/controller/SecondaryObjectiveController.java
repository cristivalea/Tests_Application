package com.example.testsapi.controller;

import com.example.testsapi.entity.SecondaryObjective;
import com.example.testsapi.service.SecondaryObjectiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/secondary-objectives")
public class SecondaryObjectiveController {
    @Autowired
    private SecondaryObjectiveService service;

    @PostMapping
    public ResponseEntity<SecondaryObjective> addObjective(@RequestBody SecondaryObjective objective) {
        return ResponseEntity.ok(service.addObjective(objective));
    }

    @PutMapping("/{id}/{idQuestion}")
    public ResponseEntity<?> updateObjective(@PathVariable Integer id, @PathVariable String idQuestion, @RequestBody SecondaryObjective updatedObjective) {
        try {
            SecondaryObjective updated = service.updateObjective(id, idQuestion, updatedObjective);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}/{idQuestion}")
    public ResponseEntity<?> deleteObjective(
            @PathVariable Integer id,
            @PathVariable String idQuestion) {

        try {
            service.deleteObjective(id, idQuestion);
            return ResponseEntity.ok("Secondary Objective delete successfully!");
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}
