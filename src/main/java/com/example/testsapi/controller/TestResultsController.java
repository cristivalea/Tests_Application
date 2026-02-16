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

    @GetMapping("/learning-curve/{idUser}")
    public List<Map<String, Object>> getLearningData(@PathVariable String idUser) { // Am schimbat în Long dacă e cazul
        List<TestResults> results = testResultsService.findByUserIdOrderByIdAsc(idUser);
        List<Map<String, Object>> points = new ArrayList<>();

        for (int i = 0; i < results.size(); i++) {
            TestResults currentResult = results.get(i);
            Map<String, Object> point = new HashMap<>();

            point.put("attempt", i + 1);
            point.put("score", currentResult.getPunctaj());

            // Extragerea obiectivului din idTest (ex: "U00033_OB1_498")
            String idTest = currentResult.getIdTest();
            String obiective = extractObjectives(idTest);

            point.put("objective", extractObjectives(currentResult.getIdTest()));
            points.add(point);
        }
        return points;
    }
    private String extractObjectives(String idTest) {
        if (idTest == null || idTest.length() <= 9) { // 6 (user) + 3 (id) = 9 caractere minim
            return "Nespecificat";
        }

        // 1. Tăiem primele 6 și ultimele 3 caractere
        // Exemplu: "U00033_1_2_3_573" -> "_1_2_3_"
        String middlePart = idTest.substring(6, idTest.length() - 3);

        // 2. Curățăm underscore-urile de la începutul și sfârșitul bucății rămase
        // "_1_2_3_" -> "1_2_3"
        if (middlePart.startsWith("_")) middlePart = middlePart.substring(1);
        if (middlePart.endsWith("_")) middlePart = middlePart.substring(0, middlePart.length() - 1);

        // 3. Facem split pe underscore pentru a izola fiecare obiectiv
        String[] parts = middlePart.split("_");

        List<String> objectives = new ArrayList<>();
        for (String part : parts) {
            if (!part.isEmpty()) {
                objectives.add("OB" + part); // Transformă "1" în "OB1"
            }
        }

        return String.join(", ", objectives); // Rezultat: "OB1, OB2, OB3"
    }

}
