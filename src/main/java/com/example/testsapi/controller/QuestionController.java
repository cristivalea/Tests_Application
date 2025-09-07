package com.example.testsapi.controller;

import com.example.testsapi.entity.Question;
import com.example.testsapi.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/searchByPeriodAndObjectives")
    public List<Question> searchByPeriodAndObjectives(@RequestParam Integer period, @RequestParam Integer idObjectiv, @RequestParam Integer idSubObjectiv) {
        return questionService.searchByPeriodAndObjectives(period, idObjectiv, idSubObjectiv);
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
    public ResponseEntity<Question> addSecondaryObjective(@PathVariable String idQuestion, @RequestParam Integer idObjective, @RequestParam String nameObjective) throws Exception{
        Question updated = questionService.addSecondaryObjective(idQuestion, idObjective, nameObjective);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("delete-secondary/{idQuestion}/{idMainObj}")
    public ResponseEntity<Question> deleteSecondaryObjective(@PathVariable String idQuestion, @PathVariable Integer idMainObj) {
        Question updated = questionService.deleteSecondaryObjective(idQuestion, idMainObj);
        return ResponseEntity.ok(updated);
    }
}