package com.example.testsapi.service;

import com.example.testsapi.entity.MainObjective;
import com.example.testsapi.entity.Question;
import com.example.testsapi.entity.QuestionId;
import com.example.testsapi.repository.MainObjectiveRepository;
import com.example.testsapi.repository.QuestionRepository;
import com.example.testsapi.repository.SecondaryObjectiveRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.testsapi.entity.SecondaryObjective;


import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private MainObjectiveRepository mainObjectiveRepository;
    @Autowired
    private SecondaryObjectiveRepository secondaryObjectiveRepository;
    @Autowired
    private ObjectMapper objectMapper;

    public Question saveQuestion(String json) throws Exception {

        JsonNode root = objectMapper.readTree(json);
        String idQuestion = root.get("id").asText();


        Integer nextId = (int) (questionRepository.count() + 1);

        Question question = new Question();
        question.setQuestionId(new QuestionId(nextId, idQuestion));
        question.setQuestion(json);

        return questionRepository.save(question);
    }


    public String getFormattedQuestion(Integer id, String idQuestion) throws Exception {
        Optional<Question> optionalQuestion = questionRepository.findById(new QuestionId(id, idQuestion));

        if (optionalQuestion.isEmpty()) {
            throw new RuntimeException("Error search question!");
        }

        String rawJson = optionalQuestion.get().getQuestion();

        JsonNode root = objectMapper.readTree(rawJson);

        JsonNode contentArray = root.path("Content");
        if (contentArray.isArray() && contentArray.size() > 0) {
            JsonNode firstContent = contentArray.get(0);

            String originalCode = firstContent.path("cod").asText();

            String formattedCode = originalCode;

            if (firstContent.isObject()) {
                ((ObjectNode) firstContent).put("cod", formattedCode);
            }
        }

        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(root);
    }

    public List<Question> getQuestionsByMainObjective(Integer idObjectiv) {

        List<MainObjective> objectives = mainObjectiveRepository.findByIdObjectiv(idObjectiv);

        List<String> idQuestions = objectives.stream()
                .map(MainObjective::getIdQuestion)
                .distinct()
                .toList();

        return questionRepository.findAll().stream()
                .filter(q -> idQuestions.contains(q.getQuestionId().getIdQuestion()))
                .collect(Collectors.toList());
    }


    public List<Question> searchByObjectives(Integer idObjectiv, Integer idSubObjectiv) {

        Set<String> mainQuestions = mainObjectiveRepository.findByIdObjectiv(idObjectiv)
                .stream()
                .map(MainObjective::getIdQuestion)
                .collect(Collectors.toSet());
        Set<String> secondaryQuestions = secondaryObjectiveRepository.findByIdObjectiv(idSubObjectiv)
                .stream()
                .map(SecondaryObjective::getIdQuestion)
                .collect(Collectors.toSet());
        Set<String> commonIds = mainQuestions.stream()
                .filter(secondaryQuestions::contains)
                .collect(Collectors.toSet());

        return questionRepository.findAll().stream()
                .filter(q -> commonIds.contains(q.getQuestionId().getIdQuestion()))
                .toList();
    }

    public List<Question> searchByDifficultyAndObjectives(String difficulty, String mainObjective, String subObjective) {
        return questionRepository.findAll().stream()
                .filter(q -> {
                    try {
                        JsonNode root = objectMapper.readTree(q.getQuestion());

                        // verifică dificultatea (câmpul "type")
                        String type = root.path("type").asText();
                        if (!type.equalsIgnoreCase(difficulty)) {
                            return false;
                        }

                        // verifică obiectivul principal
                        JsonNode mainObjectives = root.path("mainObjectives");
                        if (mainObjectives.isArray()) {
                            for (JsonNode mo : mainObjectives) {
                                String mainName = mo.path("name").asText();
                                if (mainName.equalsIgnoreCase(mainObjective)) {
                                    // dacă subObjective e gol -> îl ignorăm
                                    if (subObjective == null || subObjective.isBlank()) {
                                        return true;
                                    }

                                    // caută în subObjectives
                                    JsonNode subObjectives = mo.path("subObjectives");
                                    if (subObjectives.isArray()) {
                                        for (JsonNode so : subObjectives) {
                                            if (so.path("name").asText().equalsIgnoreCase(subObjective)) {
                                                return true;
                                            }
                                        }
                                    }

                                    // caută și în secondaryObjectives (ca în JSON-ul tău)
                                    JsonNode secondaryObjectives = root.path("secondaryObjectives");
                                    if (secondaryObjectives.isArray()) {
                                        for (JsonNode so : secondaryObjectives) {
                                            if (so.path("name").asText().equalsIgnoreCase(subObjective)) {
                                                return true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return false;
                })
                .toList();
    }

}
