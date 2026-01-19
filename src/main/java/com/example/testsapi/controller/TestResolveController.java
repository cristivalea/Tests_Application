package com.example.testsapi.controller;

import com.example.testsapi.dto.TestResolveItemDTO;
import com.example.testsapi.dto.TestResolveResultDTO;
import com.example.testsapi.service.TestResolveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/testResolve")
public class TestResolveController {

    @Autowired
    private TestResolveService testResolveService;

    @PostMapping("/evaluate")
    public ResponseEntity<TestResolveResultDTO> evaluateTest(@RequestBody List<TestResolveItemDTO> request) {
        return ResponseEntity.ok(testResolveService.evaluateTest(request));
    }
}
