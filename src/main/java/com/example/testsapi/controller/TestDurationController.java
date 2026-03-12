package com.example.testsapi.controller;

import com.example.testsapi.entity.TestDuration;
import com.example.testsapi.service.TestDurationService;
import com.example.testsapi.service.TestResultsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test_duration")
public class TestDurationController {

    @Autowired
    private TestDurationService testDurationService;

    public TestDurationController(TestDurationService testDurationService) {
        this.testDurationService = testDurationService;
    }

    @PostMapping("/addTestDuration")
    public ResponseEntity<TestDuration> addTestDuration(@RequestBody TestDuration testDuration){
        return ResponseEntity.ok(this.testDurationService.addTestDuration(testDuration));
    }
}
