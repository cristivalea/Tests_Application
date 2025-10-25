package com.example.testsapi.controller;

import com.example.testsapi.entity.Objectiv;
import com.example.testsapi.entity.Question;
import com.example.testsapi.repository.QuestionRepository;
import com.example.testsapi.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
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
        // 1️⃣ Obține întrebările din baza de date
        List<Question> questions = questionRepository.findAllByQuestionId_IdQuestionIn(questionIds);

        // 2️⃣ Generează ID-ul
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

    @GetMapping("/beginner/{level}/{idObjectiv}")
    public List<Question> generateBeginnerTest(
            @PathVariable String level,
            @PathVariable int idObjectiv
    ) {
        return testService.generateTestBeginnerAndIntermediate(level, idObjectiv);
    }

}