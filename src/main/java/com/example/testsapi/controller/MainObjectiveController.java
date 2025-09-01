package com.example.testsapi.controller;

import com.example.testsapi.entity.MainObjective;
import com.example.testsapi.service.MainObjectiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
