package com.example.testsapi.controller;

import com.example.testsapi.dto.TestResolveItemDTO;
import com.example.testsapi.dto.TestResolveResultDTO;
import com.example.testsapi.entity.TestResults;
import com.example.testsapi.service.TestResultsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/testResults")
public class TestResultsController {

    @Autowired
    private TestResultsService testResultsService;

    public TestResultsController(TestResultsService testResultsService) {
        this.testResultsService = testResultsService;
    }

    @PostMapping("/add_results")
    public ResponseEntity<TestResults> addResults(@RequestBody TestResults request) {
        return ResponseEntity.ok(testResultsService.addTestResult(request));
    }
}
