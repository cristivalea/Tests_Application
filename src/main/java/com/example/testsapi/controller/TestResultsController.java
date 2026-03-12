package com.example.testsapi.controller;

import com.example.testsapi.dto.TestResolveItemDTO;
import com.example.testsapi.dto.TestResolveResultDTO;
import com.example.testsapi.entity.TestResults;
import com.example.testsapi.service.TestResultsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

//    @GetMapping("/learning-curve/{idUser}")
//    public List<Map<String, Object>> getLearningData(@PathVariable String idUser) { // Am schimbat în Long dacă e cazul
//        List<TestResults> results = testResultsService.findByUserIdOrderByIdAsc(idUser);
//        List<Map<String, Object>> points = new ArrayList<>();
//
//        for (int i = 0; i < results.size(); i++) {
//            TestResults currentResult = results.get(i);
//            Map<String, Object> point = new HashMap<>();
//
//            point.put("attempt", i + 1);
//            point.put("score", currentResult.getPunctaj());
//
//            // Extragerea obiectivului din idTest (ex: "U00033_OB1_498")
//            String idTest = currentResult.getIdTest();
//            String obiective = extractObjectives(idTest);
//
//            point.put("objective", extractObjectives(currentResult.getIdTest()));
//            points.add(point);
//        }
//        return points;
//    }

    @GetMapping("/learning-curve/{idUser}")
    public Map<String, Object> getLearningData(@PathVariable String idUser) {
        List<TestResults> results = testResultsService.findByUserIdOrderByIdAsc(idUser);
        List<Map<String, Object>> actualPoints = new ArrayList<>();

        for (int i = 0; i < results.size(); i++) {
            Map<String, Object> point = new HashMap<>();
            point.put("attempt", i + 1);
            point.put("score", results.get(i).getPunctaj());
            point.put("objective", extractObjectives(results.get(i).getIdTest()));
            point.put("type", "actual");
            actualPoints.add(point);
        }

        List<Map<String, Object>> predictions = new ArrayList<>();
        if (results.size() >= 2) {
            predictions = calculatePredictions(results, 3);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("actual", actualPoints);
        response.put("predictions", predictions);

        return response;
    }

    private List<Map<String, Object>> calculatePredictions(List<TestResults> results, int count) {
        int n = results.size();
        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;

        for (int i = 0; i < n; i++) {
            double x = i + 1;
            double y = results.get(i).getPunctaj();
            sumX += x;
            sumY += y;
            sumXY += x * y;
            sumX2 += x * x;
        }

        double m = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX);
        double b = (sumY - m * sumX) / n;

        List<Map<String, Object>> predList = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            int nextAttempt = n + i;
            double predictedScore = m * nextAttempt + b;
            predictedScore = Math.max(0, Math.min(100, predictedScore));

            Map<String, Object> predPoint = new HashMap<>();
            predPoint.put("attempt", nextAttempt);
            predPoint.put("score", Math.round(predictedScore * 100.0) / 100.0);
            predPoint.put("type", "prediction");
            predList.add(predPoint);
        }
        return predList;
    }


    private String extractObjectives(String idTest) {
        if (idTest == null || idTest.length() <= 9) { // 6 (user) + 3 (id) = 9 caractere minim
            return "Nespecificat";
        }

        String middlePart = idTest.substring(6, idTest.length() - 3);

        if (middlePart.startsWith("_")) middlePart = middlePart.substring(1);
        if (middlePart.endsWith("_")) middlePart = middlePart.substring(0, middlePart.length() - 1);

        String[] parts = middlePart.split("_");

        List<String> objectives = new ArrayList<>();
        for (String part : parts) {
            if (!part.isEmpty()) {
                objectives.add("OB" + part);
            }
        }

        return String.join(", ", objectives);
    }



}
