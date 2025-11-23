package com.example.testsapi.controller;

import com.example.testsapi.entity.*;
import com.example.testsapi.repository.QuestionRepository;
import com.example.testsapi.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private TestService testService;

    @Autowired
    private QuestionRepository questionRepository;

    /**
     * Generează un ID pentru un utilizator, folosind întrebările din DB
     *
     * @param userId      - id-ul utilizatorului (de exemplu "U1")
     * @param questionIds - lista de ID-uri de întrebări (din DB)
     */
    @GetMapping("/generate-id")
    public String generateId(
            @RequestParam String userId,
            @RequestParam List<String> questionIds
    ) {
        List<Question> questions = questionRepository.findAllByQuestionId_IdQuestionIn(questionIds);
        String id = testService.generateIdForUser(userId, questions);

        return "Generated ID: " + id;
    }

    @GetMapping("/principles-objectives")
    public List<Objectiv> getPrinciplesObjectives(@RequestParam List<String> subObjectiveIds) {
        List<Objectiv> subObj = new ArrayList<>();
        for (String id : subObjectiveIds) {
            subObj.add(new Objectiv("", id, false, null));
        }
        return testService.addPrinciplesObjectivesInList(subObj);
    }

    @PostMapping("/secondary_objectives")
    public List<Object> getSecondaryObjectives(@RequestBody List<Question> questions){
        return testService.extractSecondaryObjectivesFromQuestions(questions);
    }

    //this function write thr totoal times of test (single or more or total objetives). Ruta este in acesta maniera indiferent de caz
    //http://localhost:8080/test/writeTest/U00033/SOFTQUESTION?objectives=8
    //http://localhost:8080/test/writeTest/U00033/MIDLELEVELQUESTION?objectives=8
    //http://localhost:8080/test/writeTest/U00033/HARD?objectives=8
    @GetMapping("/writeTest/{userId}/{level}")
    public TestOfQuestions writeTest(
            @PathVariable String userId,
            @PathVariable String level,
            @RequestParam(name = "objectives") List<Integer> objectives
    ) {
        int[] objArray = objectives.stream().mapToInt(Integer::intValue).toArray();
//        return testService.writeTestSingleObjective(userId, level, objArray);
        return  testService.writeTestSingleObjective(userId, level, objArray);
    }


    @GetMapping("/distance")
    public double calculateDistance(
            @RequestParam String questionId1,
            @RequestParam String questionId2
    ) {
        Question q1 = questionRepository.findByQuestionId_IdQuestion(questionId1)
                .orElseThrow(() -> new RuntimeException("Întrebarea 1 nu există în DB!"));
        Question q2 = questionRepository.findByQuestionId_IdQuestion(questionId2)
                .orElseThrow(() -> new RuntimeException("Întrebarea 2 nu există în DB!"));

        return testService.distanceToQuestion(q1, q2);
    }

    @GetMapping("/generate/beginner/{objectiveId}")
    public List<Question> generateBeginnerTest(@PathVariable int idObjectiv) {
        return testService.generateTestBeginnerAndIntermediate(LevelTest.BEGINNER.getDenumire(), idObjectiv);
    }
    @GetMapping("/generate/intermediate/{objectiveId}")
    public List<Question> generateIntermediateTest(@PathVariable String level, @PathVariable int idObjectiv) {
        return testService.generateTestBeginnerAndIntermediate(level, idObjectiv);
    }

    @GetMapping("/generate/hard/{objectiveId}")
    public List<Question> generateHardTest(@PathVariable int objectiveId) {
        return testService.generateHardTest(objectiveId);
    }

    //http://localhost:8080/test/beginner/more_obj?level=SOFTQUESTION&objectives=8,9
    @GetMapping("/beginner/more_obj")
    public ResponseEntity<List<Question>> generateBeginnerTestMoreObj(@RequestParam String level, @RequestParam(name = "objectives") List<Integer> objectives){
        try {
            int[] objArray = objectives.stream().mapToInt(Integer::intValue).toArray();
            List<Question> questions = testService.generateTestBeginnerAndIntermediateMoreObj(level, objArray);
            return ResponseEntity.ok(questions);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    //http://localhost:8080/test/beginner/more_obj?level=MIDLELEVELQUESTION&objectives=8,9
    @GetMapping("/intermediate/more_obj")
    public ResponseEntity<List<Question>> generateIntermediateTestMoreObj(@RequestParam String level, @RequestParam(name = "objectives") List<Integer> objectives){
        try {
            int[] objArray = objectives.stream().mapToInt(Integer::intValue).toArray();
            List<Question> questions = testService.generateTestBeginnerAndIntermediateMoreObj(level, objArray);
            return ResponseEntity.ok(questions);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    //http://localhost:8080/test/hard/more_obj?objectives=8,9
    @GetMapping("/hard/more_obj")
    public ResponseEntity<List<Question>> generateHardTestMoreObj(
            @RequestParam(name = "objectives") List<Integer> objectives
    ) {
        try {
            int[] objArray = objectives.stream().mapToInt(Integer::intValue).toArray();
            List<Question> questions = testService.generateHardTestMoreObj(objArray);
            return ResponseEntity.ok(questions);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    //http://localhost:8080/test/hard/all_objectives/SOFTQUESTION
    @GetMapping("/beginner/all_objectives/{level}")
    public ResponseEntity<List<Question>> generateBeginnerTestAllObjectives(@PathVariable String level){
        return ResponseEntity.ok(testService.generateTestBeginnerAndIntermediateAllObjectives(level));
    }

    //http://localhost:8080/test/hard/all_objectives/MIDLELEVELQUESTION
    @GetMapping("/intermediate/all_objectives/{level}")
    public ResponseEntity<List<Question>> generateIntermediateTestAllObjectives(@PathVariable String level){
        return ResponseEntity.ok(testService.generateTestBeginnerAndIntermediateAllObjectives(level));
    }

    //http://localhost:8080/test/hard/all_objectives
    @GetMapping("/hard/all_objectives")
    public ResponseEntity<List<Question>> generateHardTestAllObjectives(){
        return ResponseEntity.ok(testService.generateHardTestAllObj());
    }

    //http://localhost:8080/test/writeFinalTest/U00031/SOFTQUESTION
    //http://localhost:8080/test/writeFinalTest/U00031/MIDLELEVELQUESTION
    //http://localhost:8080/test/writeFinalTest/U00031/HARD
    @GetMapping("/writeFinalTest/{userId}/{level}")
    public Test writeFinalTest(@PathVariable String userId, @PathVariable String level) {
        return testService.writingFinalTest(userId, level);
    }

//    @PostMapping("/{level}/by-sub-objectives")
//    public ResponseEntity<?> generateTestBySecondaryObjectivesUrl(
//            @PathVariable String level,
//            @RequestParam String userId,
//            @RequestParam(name = "subObjIds") int[] subObjIds) { // Spring convertește automat "101,203,305" la int[]
//
//        try {
//            // 1. Validare Nivel
//            String normalizedLevel = level.toUpperCase();
//            if (!normalizedLevel.equals("BEGINNER") && !normalizedLevel.equals("INTERMEDIATE")) {
//                return new ResponseEntity<>("Nivelul specificat trebuie să fie 'BEGINNER' sau 'INTERMEDIATE'.", HttpStatus.BAD_REQUEST);
//            }
//
//            if (subObjIds == null || subObjIds.length == 0) {
//                return new ResponseEntity<>("Parametrul 'subObjIds' (obiective secundare) este obligatoriu.", HttpStatus.BAD_REQUEST);
//            }
//
//            // 2. Apelarea metodei din Service
//            Test generatedTest = testService.writeTestBySecondaryObjectives(userId, normalizedLevel, subObjIds);
//
//            return new ResponseEntity<>(generatedTest, HttpStatus.CREATED);
//
//        } catch (IllegalArgumentException e) {
//            return new ResponseEntity<>("Eroare de logică: " + e.getMessage(), HttpStatus.BAD_REQUEST);
//        } catch (Exception e) {
//            System.err.println("Eroare la generarea testului: " + e.getMessage());
//            e.printStackTrace();
//            return new ResponseEntity<>("A apărut o eroare internă la generarea testului.", HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
}