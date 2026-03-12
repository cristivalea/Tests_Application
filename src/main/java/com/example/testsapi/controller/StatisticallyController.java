package com.example.testsapi.controller;

import com.example.testsapi.entity.ObjectiveAnalysis;
import com.example.testsapi.entity.TestDuration;
import com.example.testsapi.entity.TestResults;
import com.example.testsapi.repository.TestDurationRepository;
import com.example.testsapi.repository.TestResultsRepository;
import com.example.testsapi.service.BTKService;
import com.example.testsapi.service.EvaluationService;
import com.example.testsapi.service.TestResultsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/statistics")
public class StatisticallyController {
    @Autowired
    private TestResultsService testResultsService;
    @Autowired
    private BTKService bktService;
    @Autowired private EvaluationService evalService;
    @Autowired private TestResultsRepository resRepo;
    @Autowired private TestDurationRepository durRepo;



    public StatisticallyController(TestResultsService testResultsService, BTKService bktService) {
        this.testResultsService = testResultsService;
        this.bktService = bktService;
    }

    @GetMapping("/analysis/{idUser}")
    public Map<String, Object> analyzeUser(@PathVariable String idUser) {
        List<TestResults> results = testResultsService.findByUserIdOrderByIdAsc(idUser);
        Map<String, Double> masteryMap = new HashMap<>();

        for (TestResults res : results) {
            String fullObjString = extractObjectives(res.getIdTest());

            String[] individualObjectives = fullObjString.split(", ");

            for (String singleObj : individualObjectives) {
                if (singleObj.equals("Nespecificat")) continue;

                double currentP = masteryMap.getOrDefault(singleObj, 0.5);
                boolean isCorrect = res.getPunctaj() > 50;

                masteryMap.put(singleObj, bktService.calculateNewMastery(currentP, isCorrect));
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("mastery", masteryMap);
        response.put("advice", generateAdvice(masteryMap));
        return response;
    }

    private String extractObjectives(String idTest) {
        if (idTest == null || idTest.length() <= 9) {
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

        return String.join(", ", objectives); // Rezultat: "OB1, OB2, OB3"
    }

    private String generateAdvice(Map<String, Double> masteryMap) {
        if (masteryMap.isEmpty()) {
            return "Încă nu ai susținut suficiente teste pentru o analiză AI. Continuă să exersezi!";
        }

        String weakestObjective = "";
        double minMastery = 1.1; // Peste maximul de 1.0

        String strongestObjective = "";
        double maxMastery = -0.1;

        // Analizăm harta de competențe pentru a găsi extremele
        for (Map.Entry<String, Double> entry : masteryMap.entrySet()) {
            if (entry.getValue() < minMastery) {
                minMastery = entry.getValue();
                weakestObjective = entry.getKey();
            }
            if (entry.getValue() > maxMastery) {
                maxMastery = entry.getValue();
                strongestObjective = entry.getKey();
            }
        }

        // Generăm sfatul în funcție de praguri
        StringBuilder advice = new StringBuilder("### Sfatul Tutorelui AI: \n");

        if (minMastery < 0.4) {
            advice.append("⚠️ Atenție: Ai dificultăți majore la **").append(weakestObjective)
                    .append("**. Îți recomandăm să reiei materialul teoretic înainte de a mai susține un test.");
        } else if (minMastery < 0.7) {
            advice.append("💡 Obiectivul **").append(weakestObjective)
                    .append("** necesită mai multă practică pentru a fi stăpânit complet.");
        } else if (minMastery >= 0.7){
            advice.append("🌟 Te descurci excelent! Stăpânești bine toate obiectivele analizate.");
        }

        if (!strongestObjective.equals(weakestObjective) && maxMastery > 0.85) {
            advice.append("\n\n✅ Punctul tău forte este: **").append(strongestObjective).append("**.");
        }

        return advice.toString();
    }

    @GetMapping("/personal-report/{userId}")
    public ResponseEntity<List<ObjectiveAnalysis>> getReport(@PathVariable String userId) {
        try {
            List<TestResults> results = resRepo.findAll();
            List<TestDuration> durations = durRepo.findAll();

            List<ObjectiveAnalysis> report = evalService.computeDetailedCluster(userId, results, durations);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/general/{userId}")
    public ResponseEntity<?> getGeneralAnalysis(@PathVariable String userId) {
        try {
            // 1. Preluăm toate datele necesare din baza de date pentru clustering
            List<TestResults> allResults = resRepo.findAll();
            List<TestDuration> allDurations = durRepo.findAll();

            // 2. Apelăm serviciul pentru a calcula clusterul
            ObjectiveAnalysis analysis = evalService.computeGeneralCluster(userId, allResults, allDurations);

            if (analysis == null) {
                return ResponseEntity.status(404).body("Nu s-au găsit date pentru utilizatorul: " + userId);
            }

            return ResponseEntity.ok(analysis);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Eroare la calcularea analizei: " + e.getMessage());
        }
    }

}
