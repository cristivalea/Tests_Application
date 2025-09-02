package com.example.testsapi.service;

import com.example.testsapi.entity.MainObjective;
import com.example.testsapi.entity.Question;
import com.example.testsapi.entity.QuestionId;
import com.example.testsapi.repository.MainObjectiveRepository;
import com.example.testsapi.repository.QuestionRepository;
import com.example.testsapi.repository.SecondaryObjectiveRepository;
import com.example.testsapi.repository.TimeLengthQuestionRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.testsapi.entity.SecondaryObjective;


import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import com.example.testsapi.entity.TimeLengthQuestion;
import com.example.testsapi.repository.QuestionRepository;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private MainObjectiveRepository mainObjectiveRepository;
    @Autowired
    private SecondaryObjectiveRepository secondaryObjectiveRepository;
    @Autowired
    private TimeLengthQuestionRepository timeLengthQuestionRepository;
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

    public List<Question> searchByPeriodAndObjectives(Integer period, Integer idObjectiv, Integer idSubObjectiv) {

        // întrebările legate de obiectiv principal
        Set<String> mainQuestions = mainObjectiveRepository.findByIdObjectiv(idObjectiv)
                .stream()
                .map(MainObjective::getIdQuestion)
                .collect(Collectors.toSet());

        // întrebările legate de obiectiv secundar
        Set<String> secondaryQuestions = secondaryObjectiveRepository.findByIdObjectiv(idSubObjectiv)
                .stream()
                .map(SecondaryObjective::getIdQuestion)
                .collect(Collectors.toSet());

        // intersecția între principal și secundar
        Set<String> commonIds = mainQuestions.stream()
                .filter(secondaryQuestions::contains)
                .collect(Collectors.toSet());

        // întrebările legate de perioada (timeLength)
        Set<String> periodQuestions = timeLengthQuestionRepository.findByTimeLength(period)
                .stream()
                .map(TimeLengthQuestion::getIdQuestion)
                .collect(Collectors.toSet());

        // intersecția finală
        Set<String> finalIds = commonIds.stream()
                .filter(periodQuestions::contains)
                .collect(Collectors.toSet());

        // întoarcem întrebările
        return questionRepository.findAll().stream()
                .filter(q -> finalIds.contains(q.getQuestionId().getIdQuestion()))
                .toList();
    }

    public void deleteByIdQuestion(String idQuestion) {
        List<Question> questions = questionRepository.findAll()
                .stream()
                .filter(q -> q.getQuestionId().getIdQuestion().equals(idQuestion))
                .toList();

        if (questions.isEmpty()) {
            throw new RuntimeException("Does not exists question with = " + idQuestion);
        }

        questionRepository.deleteAll(questions);
    }

    public Question updateFormat(String idQuestion, String newFormat) throws Exception {
        Question q = questionRepository.findByQuestionId_IdQuestion(idQuestion)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        // Parse JSON-ul ca Map
        Map<String, Object> map = objectMapper.readValue(q.getQuestion(), Map.class);

        // Modificăm doar format
        map.put("format", newFormat);

        // Rescriem JSON-ul
        q.setQuestion(objectMapper.writeValueAsString(map));

        return questionRepository.save(q);
    }

    public Question updateType(String idQuestion, String newType) throws Exception {
        Question q = questionRepository.findByQuestionId_IdQuestion(idQuestion)
                .orElseThrow(() -> new RuntimeException("Question not found"));
        Map<String, Object> map = objectMapper.readValue(q.getQuestion(), Map.class);

        map.put("type", newType);

        q.setQuestion(objectMapper.writeValueAsString(map));

        return questionRepository.save(q);
    }

    public Question updateHint(String idQuestion, String newHint) throws Exception {
        Question q = questionRepository.findByQuestionId_IdQuestion(idQuestion)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        Map<String, Object> map = objectMapper.readValue(q.getQuestion(), Map.class);

        map.put("hint", newHint);

        q.setQuestion(objectMapper.writeValueAsString(map));

        return questionRepository.save(q);
    }
}
