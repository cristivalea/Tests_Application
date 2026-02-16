package com.example.testsapi.controller;

import com.example.testsapi.entity.TestResults;
import com.example.testsapi.service.BTKService;
import com.example.testsapi.service.TestResultsService;
import org.springframework.beans.factory.annotation.Autowired;
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

    public StatisticallyController(TestResultsService testResultsService, BTKService bktService) {
        this.testResultsService = testResultsService;
        this.bktService = bktService;
    }

    @GetMapping("/analysis/{idUser}")
    public Map<String, Object> analyzeUser(@PathVariable String idUser) {
        List<TestResults> results = testResultsService.findByUserIdOrderByIdAsc(idUser);
        Map<String, Double> masteryMap = new HashMap<>(); // Stochează Masteria pentru OB1, OB2 etc.

        for (TestResults res : results) {
            String obj = extractObjectives(res.getIdTest()); // Folosim metoda ta de extragere
            double currentP = masteryMap.getOrDefault(obj, 0.5); // Începem de la 50% certitudine

            // Dacă punctajul > 50, considerăm răspunsul "corect" pentru algoritm
            boolean isCorrect = res.getPunctaj() > 50;
            masteryMap.put(obj, bktService.calculateNewMastery(currentP, isCorrect));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("mastery", masteryMap);
        response.put("advice", generateAdvice(masteryMap));
        return response;
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
        } else {
            advice.append("🌟 Te descurci excelent! Stăpânești bine toate obiectivele analizate.");
        }

        if (!strongestObjective.equals(weakestObjective) && maxMastery > 0.85) {
            advice.append("\n\n✅ Punctul tău forte este: **").append(strongestObjective).append("**.");
        }

        return advice.toString();
    }

}
