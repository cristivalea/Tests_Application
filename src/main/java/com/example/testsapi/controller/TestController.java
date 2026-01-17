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

    //http://localhost:8080/test/principles-objectives?subObjectiveIds=92
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
        for(int i = 0; i < objArray.length; i++) {
            System.err.println(objArray[i]);
        }
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

    //http://localhost:8080/test/intermediate/beginner/9
    @GetMapping("/generate/beginner/{objectiveId}")
    public List<Question> generateBeginnerTest(@PathVariable("objectiveId") int idObjectiv) {
        return testService.generateTestBeginnerAndIntermediate("SOFTQUESTION", idObjectiv);
    }
    //http://localhost:8080/test/generate/intermediate/9
    @GetMapping("/generate/intermediate/{objectiveId}")
    public List<Question> generateIntermediateTest(@PathVariable("objectiveId") int idObjectiv) {
        return testService.generateTestBeginnerAndIntermediate("MIDLELEVELQUESTION", idObjectiv);
    }

    //http://localhost:8080/test/generate/hard/9
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
    public TestOfQuestions writeFinalTest(@PathVariable String userId, @PathVariable String level) {
        return testService.writingFinalTest(userId, level);
    }

    //http://localhost:8080/extract_question_by_subObj?obj=91,92,93&level=MIDLELEVELQUESTION
//    @GetMapping("/extract_question_by_subObj")
//    public List<List<Question>> getQuestionBySubObj(@RequestParam(name = "obj") int[] obj, @RequestParam(name = "level") String level) {
//        return testService.searchQuestionsBySecondaryObjectivesAndLevel(obj, level);
//    }

    /**
     * Endpoint pentru generarea testului pe baza subobiectivelor și nivelului.
     * URL-ul ar trebui să arate astfel:
     *
     */
    //http://localhost:8080/test/writeTestWithSubObjectives?userId=U00033&level=SOFTQUESTION&subobjectives=91,92,93
    @GetMapping("/writeTestWithSubObjectives")
    public ResponseEntity<TestOfQuestions> writeTestWithSubObjectives(
            @RequestParam String userId,  // ID-ul utilizatorului
            @RequestParam String level,   // Nivelul întrebărilor ("SOFTQUESTION", "MIDLELEVELQUESTION", "HARDQUESTION")
            @RequestParam int[] subobjectives  // Lista de subobiective
    ) {
        try {
            // Apelăm serviciul pentru a genera testul
            TestOfQuestions testOfQuestions = testService.writeTestSingleAndMoreSubobjectives(userId, level, subobjectives);

            // Returnăm testul generat în format JSON
            return ResponseEntity.ok(testOfQuestions);
        } catch (Exception e) {
            // În cazul în care există o eroare, returnăm un status de eroare
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}