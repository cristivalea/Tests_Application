package com.example.testsapi.controller;

import com.example.testsapi.entity.Metric;
import com.example.testsapi.entity.Question;
import com.example.testsapi.entity.QuestionId;
import com.example.testsapi.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @PostMapping
    public ResponseEntity<?> addQuestion(@RequestBody String json) {
        try {
            Question saved = questionService.saveQuestion(json);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Eroare: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/{idQuestion}")
    public ResponseEntity<?> getFormattedQuestion(
            @PathVariable Integer id,
            @PathVariable String idQuestion) {
        try {
            String formattedJson = questionService.getFormattedQuestion(id, idQuestion);
            return ResponseEntity.ok(formattedJson);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Eroare: " + e.getMessage());
        }
    }

    @GetMapping("/by-main-objective/{idObjectiv}")
    public ResponseEntity<List<Question>> getQuestionsByObjective(@PathVariable Integer idObjectiv) {
        List<Question> questions = questionService.getQuestionsByMainObjective(idObjectiv);
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/searchPrincipalObj")
    public List<Question> searchQuestions(@RequestParam Integer mainObjective, @RequestParam Integer subObjective) {
        return questionService.searchByObjectives(mainObjective, subObjective);
    }

    @GetMapping("/searchByPeriodAndObjectives/{period}/{idObjectiv}/{idSubObjectiv}")
    public List<Question> searchByPeriodAndObjectives(@PathVariable String period, @PathVariable Integer idObjectiv, @PathVariable Integer idSubObjectiv) {
        return questionService.searchByObjectivesAndLevel(idObjectiv, idSubObjectiv, period);
    }

    @DeleteMapping("/{idQuestion}")
    public ResponseEntity<?> deleteQuestion(@PathVariable String idQuestion) {
        try {
            questionService.deleteByIdQuestion(idQuestion);
            return ResponseEntity.ok("Question with id=" + idQuestion + " delete.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/{idQuestion}/format")
    public ResponseEntity<Question> updateFormat(@PathVariable String idQuestion, @RequestParam String newFormat) {
        try {
            Question updated = questionService.updateFormat(idQuestion, newFormat);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{idQuestion}/type")
    public ResponseEntity<Question> updateType(@PathVariable String idQuestion, @RequestParam String newType) {
        try {
            Question updated = questionService.updateType(idQuestion, newType);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{idQuestion}/hint")
    public ResponseEntity<Question> updateHint(@PathVariable String idQuestion, @RequestParam String newHint) {
        try {
            Question updated = questionService.updateHint(idQuestion, newHint);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{idQuestion}/add-objective")
    public ResponseEntity<Question> addObjective(@PathVariable String idQuestion, @RequestParam Integer idObjective, @RequestParam String nameObjective) throws Exception {
        Question updated = questionService.addMainObjective(idQuestion, idObjective, nameObjective);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{idQuestion}/{idMainObj}")
    public ResponseEntity<Question> deleteMainObjective(@PathVariable String idQuestion, @PathVariable Integer idMainObj) {
        Question updated = questionService.deleteMainObjective(idQuestion, idMainObj);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{idQuestion}/add-objective-secondary")
    public ResponseEntity<Question> addSecondaryObjective(@PathVariable String idQuestion, @RequestParam Integer idObjective, @RequestParam String nameObjective) throws Exception {
        Question updated = questionService.addSecondaryObjective(idQuestion, idObjective, nameObjective);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("delete-secondary/{idQuestion}/{idMainObj}")
    public ResponseEntity<Question> deleteSecondaryObjective(@PathVariable String idQuestion, @PathVariable Integer idMainObj) {
        Question updated = questionService.deleteSecondaryObjective(idQuestion, idMainObj);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/questions/{idQuestion}/code")
    public ResponseEntity<Question> addCode(@PathVariable String idQuestion, @RequestBody Map<String, String> body) throws Exception {
        return ResponseEntity.ok(questionService.addCode(idQuestion, body.get("code")));
    }

    @PostMapping("/questions/{idQuestion}/text")
    public ResponseEntity<Question> addText(
            @PathVariable String idQuestion,
            @RequestBody Map<String, String> body) throws Exception {
        return ResponseEntity.ok(questionService.addTextToContent(idQuestion, body.get("text")));
    }

    @PostMapping("/{idQuestion}/{}")
    public ResponseEntity<Question> addresponse(@PathVariable String idQuestion, @RequestParam Integer idObjective, @RequestParam String nameObjective) throws Exception {
        Question updated = questionService.addSecondaryObjective(idQuestion, idObjective, nameObjective);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/questions/{idQuestion}/responses")
    public ResponseEntity<Question> addResponse(@PathVariable String idQuestion, @RequestParam String response) {
        if (response == null || response.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        Question updatedQuestion = questionService.addResponse(idQuestion, response);
        return ResponseEntity.ok(updatedQuestion);
    }

    @PostMapping("/questions/{idQuestion}/answer")
    public ResponseEntity<Question> addAnswer(@PathVariable String idQuestion, @RequestParam String answer) {
        if (answer == null || answer.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        Question updatedQuestion = questionService.addAnswer(idQuestion, answer);
        return ResponseEntity.ok(updatedQuestion);
    }

    @DeleteMapping("/delete-response/{idQuestion}/{response}")
    public ResponseEntity<Question> deleteResponse(@PathVariable String idQuestion, @PathVariable String response) {
        Question updated = questionService.deleteResponse(idQuestion, response);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/delete-answer/{idQuestion}/{answer}")
    public ResponseEntity<Question> deleteAnswer(@PathVariable String idQuestion, @PathVariable String answer) {
        Question updated = questionService.deleteAnswer(idQuestion, answer);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/distance")
    public Metric getDistance(@RequestParam Integer id1, @RequestParam String idQuestion1,
                              @RequestParam Integer id2, @RequestParam String idQuestion2) {

        Question q1 = questionService.findById(new QuestionId(id1, idQuestion1))
                .orElseThrow(() -> new RuntimeException("Question 1 not found"));
        Question q2 = questionService.findById(new QuestionId(id2, idQuestion2))
                .orElseThrow(() -> new RuntimeException("Question 2 not found"));

        return questionService.distance(q1, q2);
    }
}