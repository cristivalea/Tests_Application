package com.example.testsapi.controller;

import com.example.testsapi.entity.ObjectiveAnalysis;
import com.example.testsapi.entity.Question;
import com.example.testsapi.entity.TestDuration;
import com.example.testsapi.entity.TestResults;
import com.example.testsapi.repository.*;
import com.example.testsapi.service.BTKService;
import com.example.testsapi.service.EvaluationService;
import com.example.testsapi.service.TestResultsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SessionRepository sessionRepository;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private MainObjectiveRepository mainObjectiveRepository;
    @Autowired
    private TestResolveRepository testResolveRepository;

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

    //calculare statistici despre utilizatori
    @GetMapping("/users/overview")
    public ResponseEntity<?> getUsersOverview() {
        try {
            // Total utilizatori
            long totalUsers = userRepository.count();

            // Utilizatori activi (status = ACTIVE)
            long activeUsers = userRepository.countByStatus(com.example.testsapi.entity.Status.ACTIVE);

            // Utilizatori inactivi
            long inactiveUsers = totalUsers - activeUsers;

            // Utilizatori admin (role = ADMIN)
            long adminUsers = userRepository.countByRole(com.example.testsapi.entity.Role.ADMIN);

            // Utilizatori normali
            long regularUsers = totalUsers - adminUsers;

            Map<String, Object> response = new HashMap<>();
            response.put("totalUsers", totalUsers);
            response.put("activeUsers", activeUsers);
            response.put("inactiveUsers", inactiveUsers);
            response.put("adminUsers", adminUsers);
            response.put("regularUsers", regularUsers);
            response.put("activityRate", totalUsers > 0 ?
                    String.format("%.1f%%", (activeUsers * 100.0 / totalUsers)) : "0%");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Eroare la calcularea statisticilor utilizatori: " + e.getMessage()));
        }
    }

    @GetMapping("/users/active")
    public ResponseEntity<?> getActiveUsers(
            @RequestParam(defaultValue = "30") int days) {
        try {
            // Calculăm data de start (acum - N zile)
            LocalDateTime startDate = LocalDateTime.now().minusDays(days);

            // Numărăm utilizatorii distincți cu sesiuni după această dată
            long activeUsers = sessionRepository.countDistinctUsersByStartSessionAfter(startDate);

            Map<String, Object> response = new HashMap<>();
            response.put("activeUsers", activeUsers);
            response.put("period", "last " + days + " days");
            response.put("startDate", startDate.toString());
            response.put("timestamp", LocalDateTime.now().toString());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Eroare la calcularea utilizatorilor activi: " + e.getMessage()));
        }
    }

    //verficica daca un anume utilizator este activ
    @GetMapping("/users/{userId}/is-active")
    public ResponseEntity<?> isUserActive(
            @PathVariable String userId,
            @RequestParam(defaultValue = "30") int days) {
        try {
            LocalDateTime startDate = LocalDateTime.now().minusDays(days);

            // Numără sesiunile utilizatorului în perioada specificată
            long sessionCount = sessionRepository
                    .countByUserIdAndStartSessionAfter(userId, startDate);

            boolean isActive = sessionCount > 0;

            Map<String, Object> response = new HashMap<>();
            response.put("userId", userId);
            response.put("isActive", isActive);
            response.put("sessionCount", sessionCount);
            response.put("period", "last " + days + " days");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Eroare: " + e.getMessage()));
        }
    }

    //statistici pentru teste
    @GetMapping("/tests/overview")
    public ResponseEntity<?> getTestsOverview() {
        try {
            // 1. Total teste
            long totalTests = resRepo.count();

            // 2. Scor mediu global
            Double avgScoreObj = resRepo.getGlobalAverageScore();
            double avgScore = avgScoreObj != null ? avgScoreObj : 0.0;

            // 3. Calculăm pass rate (punctaj >= 65)
            List<TestResults> allResults = resRepo.findAll();
            long passedTests = allResults.stream()
                    .filter(t -> t.getPunctaj() >= 65)
                    .count();

            double passRate = totalTests > 0 ? (passedTests * 100.0 / totalTests) : 0.0;
            long failedTests = totalTests - passedTests;

            Map<String, Object> response = new HashMap<>();
            response.put("totalTestsTaken", totalTests);
            response.put("averageScore", String.format("%.1f%%", avgScore));
            response.put("passRate", String.format("%.1f%%", passRate));
            response.put("passedTests", passedTests);
            response.put("failedTests", failedTests);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Eroare: " + e.getMessage()));
        }
    }

    @GetMapping("/tests/by-difficulty")
    public ResponseEntity<?> getTestsByDifficulty() {
        try {
            // Obține counts din repository (cu JOIN)
            List<Map<String, Object>> results = resRepo.countTestsByDifficultyLevel();

            // Transformă în format final
            Map<String, Long> levelCounts = new HashMap<>();
            levelCounts.put("beginner", 0L);
            levelCounts.put("intermediate", 0L);
            levelCounts.put("hard", 0L);

            for (Map<String, Object> row : results) {
                String level = row.get("level").toString().toLowerCase();
                Long count = ((Number) row.get("count")).longValue();
                levelCounts.put(level, count);
            }

            return ResponseEntity.ok(levelCounts);

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Eroare: " + e.getMessage()));
        }
    }

    @GetMapping("/tests/objectives/ranking")
    public ResponseEntity<?> getObjectivesRanking() {
        try {
            List<TestResults> allResults = resRepo.findAll();

            // Colectează statistici per obiectiv
            Map<String, ObjectiveStats> objectiveStatsMap = new HashMap<>();

            for (TestResults result : allResults) {
                String objectivesStr = extractObjectivesNew(result.getIdTest());
                String[] objectives = objectivesStr.split(", ");

                for (String objective : objectives) {
                    if (objective.equals("Nespecificat")) continue;

                    ObjectiveStats stats = objectiveStatsMap.getOrDefault(
                            objective,
                            new ObjectiveStats()
                    );

                    stats.testCount++;
                    stats.totalScore += result.getPunctaj();

                    objectiveStatsMap.put(objective, stats);
                }
            }

            // Creează lista de răspuns
            List<Map<String, Object>> ranking = new ArrayList<>();

            for (Map.Entry<String, ObjectiveStats> entry : objectiveStatsMap.entrySet()) {
                String objective = entry.getKey();
                ObjectiveStats stats = entry.getValue();

                double avgScore = stats.testCount > 0 ?
                        stats.totalScore / stats.testCount : 0.0;

                Map<String, Object> objectiveData = new HashMap<>();
                objectiveData.put("objective", objective);
                objectiveData.put("testCount", stats.testCount);
                objectiveData.put("avgScore", Math.round(avgScore * 10.0) / 10.0);
                objectiveData.put("difficulty", getDifficultyLabel(avgScore));

                ranking.add(objectiveData);
            }

            // Sortează după avgScore descrescător (cele mai ușoare primul)
            ranking.sort((a, b) -> Double.compare(
                    (Double) b.get("avgScore"),
                    (Double) a.get("avgScore")
            ));

            return ResponseEntity.ok(ranking);

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Eroare: " + e.getMessage()));
        }
    }

    private static class ObjectiveStats {
        int testCount = 0;
        double totalScore = 0.0;
    }

    private String extractObjectivesNew(String idTest) {
        if (idTest == null || idTest.length() < 10) {
            return "Nespecificat";
        }

        try {
            int startIndex = 6;
            int endIndex = idTest.length() - 3;

            if (endIndex <= startIndex) {
                return "Nespecificat";
            }

            String middlePart = idTest.substring(startIndex, endIndex);

            // Curăță underscores de la început/sfârșit
            middlePart = middlePart.replaceAll("^_+|_+$", "");

            if (middlePart.isEmpty()) {
                return "Nespecificat";
            }

            String[] parts = middlePart.split("_");

            List<String> objectives = new ArrayList<>();
            for (String part : parts) {
                if (part.isEmpty()) continue;

                // Exclude keywords de nivel
                if (part.matches("BEG|INT|ADV|HARD|BEGINNER|INTERMEDIATE|ADVANCED")) {
                    continue;
                }

                // ✅ VERIFICĂ DACĂ PARTEA DEJA CONȚINE "OB"
                if (part.startsWith("OB")) {
                    // Deja are prefix "OB", adaugă direct
                    objectives.add(part);
                } else if (part.matches("\\d+")) {
                    // Este doar număr, adaugă prefix "OB"
                    objectives.add("OB" + part);
                }
                // Altfel skip (ex: alte string-uri nerecunoscute)
            }

            if (objectives.isEmpty()) {
                return "Nespecificat";
            }

            return String.join(", ", objectives);

        } catch (Exception e) {
            System.err.println("❌ Error extracting objectives from: " + idTest);
            e.printStackTrace();
            return "Nespecificat";
        }
    }

    private String getDifficultyLabel(double avgScore) {
        if (avgScore >= 75) return "Easy";
        if (avgScore >= 65) return "Moderate";
        if (avgScore >= 55) return "Hard";
        if (avgScore >= 45) return "Very Hard";
        return "Extremely Hard";
    }


    // ========== STATISTICI ÎNTREBĂRI ==========

    /**
     * ✅ STATISTICĂ 1: Număr total de întrebări
     * GET /statistics/questions/total
     */
    @GetMapping("/questions/total")
    public ResponseEntity<?> getTotalQuestions() {
        try {
            long totalQuestions = questionRepository.count();

            Map<String, Object> response = new HashMap<>();
            response.put("totalQuestions", totalQuestions);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Eroare: " + e.getMessage()));
        }
    }

    /**
     * ✅ STATISTICĂ 2: Overview complet întrebări
     * GET /statistics/questions/overview
     */
    @GetMapping("/questions/overview")
    public ResponseEntity<?> getQuestionsOverview() {
        try {
            long totalQuestions = questionRepository.count();

            // Breakdown pe dificultate
            Map<String, Long> byDifficulty = new HashMap<>();
            List<String> levels = questionRepository.findDistinctLevels();

            for (String level : levels) {
                long count = questionRepository.countByLevel(level);
                byDifficulty.put(level, count);
            }

            // Breakdown pe domeniu
            Map<String, Long> byDomain = new HashMap<>();
            List<String> domains = questionRepository.findDistinctDomains();

            for (String domain : domains) {
                long count = questionRepository.countByDomain(domain);
                byDomain.put(domain, count);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("totalQuestions", totalQuestions);
            response.put("byDifficulty", byDifficulty);
            response.put("byDomain", byDomain);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Eroare: " + e.getMessage()));
        }
    }

    /**
     * ✅ STATISTICĂ 3: Breakdown pe niveluri de dificultate
     * GET /statistics/questions/by-difficulty
     */
    @GetMapping("/questions/by-difficulty")
    public ResponseEntity<?> getQuestionsByDifficulty() {
        try {
            long totalQuestions = questionRepository.count();

            if (totalQuestions == 0) {
                return ResponseEntity.ok(Map.of("message", "Nu există întrebări"));
            }

            List<String> levels = questionRepository.findDistinctLevels();
            Map<String, Map<String, Object>> response = new HashMap<>();

            for (String level : levels) {
                long count = questionRepository.countByLevel(level);
                double percentage = (count * 100.0) / totalQuestions;

                Map<String, Object> levelData = new HashMap<>();
                levelData.put("count", count);
                levelData.put("percentage", Math.round(percentage * 10.0) / 10.0);

                response.put(level, levelData);
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Eroare: " + e.getMessage()));
        }
    }

    /**
     * ✅ STATISTICĂ 4: Breakdown pe domenii
     * GET /statistics/questions/by-domain
     */
    @GetMapping("/questions/by-domain")
    public ResponseEntity<?> getQuestionsByDomain() {
        try {
            long totalQuestions = questionRepository.count();

            if (totalQuestions == 0) {
                return ResponseEntity.ok(Map.of("message", "Nu există întrebări"));
            }

            List<String> domains = questionRepository.findDistinctDomains();
            Map<String, Map<String, Object>> response = new HashMap<>();

            for (String domain : domains) {
                long count = questionRepository.countByDomain(domain);
                double percentage = (count * 100.0) / totalQuestions;

                Map<String, Object> domainData = new HashMap<>();
                domainData.put("count", count);
                domainData.put("percentage", Math.round(percentage * 10.0) / 10.0);

                response.put(domain, domainData);
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Eroare: " + e.getMessage()));
        }
    }

    /**
     * ✅ STATISTICĂ 5: Matrice domain × difficulty
     * GET /statistics/questions/matrix
     */
    @GetMapping("/questions/matrix")
    public ResponseEntity<?> getQuestionsMatrix() {
        try {
            List<String> domains = questionRepository.findDistinctDomains();
            List<String> levels = questionRepository.findDistinctLevels();

            Map<String, Map<String, Long>> matrix = new HashMap<>();

            for (String domain : domains) {
                Map<String, Long> levelCounts = new HashMap<>();

                for (String level : levels) {
                    long count = questionRepository.countByLevelAndDomain(level, domain);
                    levelCounts.put(level, count);
                }

                matrix.put(domain, levelCounts);
            }

            return ResponseEntity.ok(matrix);

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Eroare: " + e.getMessage()));
        }
    }

    /**
     * ✅ STATISTICĂ 6: Număr de întrebări pe fiecare obiectiv de învățare
     * GET /statistics/questions/by-learning-objective
     *
     * Response: [
     *   {
     *     "objectiveId": 1,
     *     "objectiveName": "Java Basics",
     *     "questionCount": 145
     *   },
     *   ...
     * ]
     */
    @GetMapping("/questions/by-learning-objective")
    public ResponseEntity<?> getQuestionsByLearningObjective() {
        try {
            List<Map<String, Object>> objectives = mainObjectiveRepository.countQuestionsByObjective();

            // Transformă în format mai citit
            List<Map<String, Object>> response = new ArrayList<>();

            for (Map<String, Object> obj : objectives) {
                Map<String, Object> item = new HashMap<>();
                item.put("objectiveId", obj.get("idObjectiv"));
                item.put("objectiveName", obj.get("nameObjectiv"));
                item.put("questionCount", ((Number) obj.get("questionCount")).intValue());

                response.add(item);
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Eroare: " + e.getMessage()));
        }
    }

    @GetMapping("/questions/performance-ranking")
    public ResponseEntity<?> getQuestionsPerformanceRanking() {
        try {
            List<Map<String, Object>> allQuestions = testResolveRepository.getAllQuestionsWithStatistics();

            List<Map<String, Object>> response = new ArrayList<>();
            int rank = 1;

            for (Map<String, Object> stats : allQuestions) {
                String idQuestion = stats.get("idQuestion").toString();
                double successRate = stats.get("successRate") != null ?
                        ((Number) stats.get("successRate")).doubleValue() : 0.0;

                String questionText = getQuestionText(idQuestion);

                Map<String, Object> questionData = new HashMap<>();
                questionData.put("rank", rank++);
                questionData.put("idQuestion", idQuestion);
                questionData.put("questionText", questionText);
                questionData.put("totalAnswers", ((Number) stats.get("totalAnswers")).intValue());
                questionData.put("correctCount", ((Number) stats.get("correctCount")).intValue());
                questionData.put("wrongCount", ((Number) stats.get("wrongCount")).intValue());
                questionData.put("successRate", successRate);
                questionData.put("difficulty", getDifficultyFromSuccessRate(successRate));

                response.add(questionData);
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Eroare: " + e.getMessage()));
        }
    }

    private String getQuestionText(String idQuestion) {
        try {
            // Caută în toate înregistrările din question care au acest idQuestion
            List<Question> questions = questionRepository.findAll().stream()
                    .filter(q -> q.getQuestionId().getIdQuestion().equals(idQuestion))
                    .toList();

            if (!questions.isEmpty()) {
                String fullText = questions.get(0).getQuestion();
                // Returnează primele 100 caractere pentru preview
                return fullText.length() > 100 ?
                        fullText.substring(0, 100) + "..." : fullText;
            }

            return "Text întrebare indisponibil";

        } catch (Exception e) {
            return "Eroare la citire text";
        }
    }

    private String getDifficultyFromSuccessRate(double successRate) {
        if (successRate >= 80) return "Very Easy";
        if (successRate >= 65) return "Easy";
        if (successRate >= 50) return "Moderate";
        if (successRate >= 35) return "Hard";
        if (successRate >= 20) return "Very Hard";
        return "Extremely Hard";
    }
}
