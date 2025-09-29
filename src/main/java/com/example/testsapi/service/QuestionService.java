package com.example.testsapi.service;

import com.example.testsapi.entity.*;
import com.example.testsapi.repository.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

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
    private QuestionLabelsRepository questionLabelsRepository;
    @Autowired
    private TimeLengthQuestionRepository timeLengthQuestionRepository;
    @Autowired
    private ObjectMapper objectMapper;

    public Question saveQuestion(String json) throws Exception {

        JsonNode root = objectMapper.readTree(json);
        String idQuestion = root.get("id").asText();
        String level = root.get("type").asText();


        Integer nextId = (int) (questionRepository.count() + 1);

        Question question = new Question();
        question.setQuestionId(new QuestionId(nextId, idQuestion));
        question.setQuestion(json);
        question.setLevel(level);

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

//    public List<Question> searchByPeriodAndObjectives(Integer period, Integer idObjectiv, Integer idSubObjectiv) {
//
//        // întrebările legate de obiectiv principal
//        Set<String> mainQuestions = mainObjectiveRepository.findByIdObjectiv(idObjectiv)
//                .stream()
//                .map(MainObjective::getIdQuestion)
//                .collect(Collectors.toSet());
//
//        // întrebările legate de obiectiv secundar
//        Set<String> secondaryQuestions = secondaryObjectiveRepository.findByIdObjectiv(idSubObjectiv)
//                .stream()
//                .map(SecondaryObjective::getIdQuestion)
//                .collect(Collectors.toSet());
//
//        // intersecția între principal și secundar
//        Set<String> commonIds = mainQuestions.stream()
//                .filter(secondaryQuestions::contains)
//                .collect(Collectors.toSet());
//
//        // întrebările legate de perioada (timeLength)
//        Set<String> periodQuestions = timeLengthQuestionRepository.findByTimeLength(period)
//                .stream()
//                .map(TimeLengthQuestion::getIdQuestion)
//                .collect(Collectors.toSet());
//
//        // intersecția finală
//        Set<String> finalIds = commonIds.stream()
//                .filter(periodQuestions::contains)
//                .collect(Collectors.toSet());
//
//        // întoarcem întrebările
//        return questionRepository.findAll().stream()
//                .filter(q -> finalIds.contains(q.getQuestionId().getIdQuestion()))
//                .toList();
//    }

    public List<Question> searchByObjectivesAndLevel(
            Integer idObjectiv, Integer idSubObjectiv, String level) {

        ObjectMapper mapper = new ObjectMapper();

        return questionRepository.findAll().stream()
                // filtrăm după level (coloană simplă în DB)
                .filter(q -> q.getLevel() != null && q.getLevel().equalsIgnoreCase(level))
                // filtrăm după JSON (mainObjectives + secondaryObjectives)
                .filter(q -> {
                    String questionJson = q.getQuestion();
                    if (questionJson == null || questionJson.isEmpty()) {
                        // dacă content-ul este null sau gol, sărim peste
                        return false;
                    }

                    try {
                        JsonNode root = mapper.readTree(questionJson);

                        // verificăm mainObjectives
                        boolean matchesMain = false;
                        JsonNode mainObjectives = root.path("mainObjectives");
                        if (mainObjectives.isArray()) {
                            for (JsonNode obj : mainObjectives) {
                                if (obj.path("id").asText().equals(String.valueOf(idObjectiv))) {
                                    matchesMain = true;
                                    break;
                                }
                            }
                        }

                        // verificăm secondaryObjectives
                        boolean matchesSecondary = false;
                        JsonNode secondaryObjectives = root.path("secondaryObjectives");
                        if (secondaryObjectives.isArray()) {
                            for (JsonNode obj : secondaryObjectives) {
                                if (obj.path("id").asText().equals(String.valueOf(idSubObjectiv))) {
                                    matchesSecondary = true;
                                    break;
                                }
                            }
                        }

                        return matchesMain && matchesSecondary;

                    } catch (Exception e) {
                        // logăm eroarea și sărim peste această întrebare
                        System.err.println("Error parsing question JSON for id=" + q.getQuestionId() + ": " + e.getMessage());
                        return false;
                    }
                })
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

    public Question addMainObjective(String idQuestion, Integer idMainObj, String nameObjective){
        Question q = questionRepository.findByQuestionId_IdQuestion(idQuestion).orElseThrow(() -> new RuntimeException("Question not found"));

        try {
            JsonNode root = objectMapper.readTree(q.getQuestion());
            JsonNode mainObjectiveNode = root.get("mainObjectives");
            if (mainObjectiveNode == null || !mainObjectiveNode.isArray()) {
                ((ObjectNode) root).putArray("mainObjectives");
                mainObjectiveNode = root.get("mainObjectives");
            }
            ObjectNode newObjective = objectMapper.createObjectNode();
            newObjective.put("id", idMainObj);
            newObjective.put("name", nameObjective);
            ((com.fasterxml.jackson.databind.node.ArrayNode) mainObjectiveNode).add(newObjective);
            q.setQuestion(objectMapper.writeValueAsString(root));
        }catch (Exception e){
            new RuntimeException(e.getMessage());
        }

        return questionRepository.save(q);
    }

    public Question deleteMainObjective(String idQuestion, Integer idMainObj){
        Question q = questionRepository.findByQuestionId_IdQuestion(idQuestion)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        try{
            JsonNode root = objectMapper.readTree(q.getQuestion());
            JsonNode mainObjectiveNode = root.get("mainObjectives");
            if(mainObjectiveNode != null && mainObjectiveNode.isArray()){
                List<JsonNode> filtered = new java.util.ArrayList<>();
                for (JsonNode obj : mainObjectiveNode) {
                    if (obj.path("id").asInt() != idMainObj) {
                        filtered.add(obj);
                    }
                }
                ((ObjectNode) root).putArray("mainObjectives").addAll(filtered);
                q.setQuestion(objectMapper.writeValueAsString(root));
            }
        }catch(Exception e){
            throw new RuntimeException(e.getMessage());
        }
        return questionRepository.save(q);
    }

    public Question addSecondaryObjective(String idQuestion, Integer idSecundaryObj, String nameObjective){
        Question q = questionRepository.findByQuestionId_IdQuestion(idQuestion)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        try {
            JsonNode root = objectMapper.readTree(q.getQuestion());
            JsonNode secondaryObjectiveNode = root.get("secondaryObjectives");
            if (secondaryObjectiveNode == null || !secondaryObjectiveNode.isArray()) {
                ((ObjectNode) root).putArray("secondaryObjectives");
                secondaryObjectiveNode = root.get("secondaryObjectives");
            }
            ObjectNode newObjective = objectMapper.createObjectNode();
            newObjective.put("id", idSecundaryObj);
            newObjective.put("name", nameObjective);
            ((com.fasterxml.jackson.databind.node.ArrayNode) secondaryObjectiveNode).add(newObjective);
            q.setQuestion(objectMapper.writeValueAsString(root));
        }catch (Exception e){
            new RuntimeException(e.getMessage());
        }

        return questionRepository.save(q);
    }

    public Question deleteSecondaryObjective(String idQuestion, Integer idSecondaryObj){
        Question q = questionRepository.findByQuestionId_IdQuestion(idQuestion)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        try{
            JsonNode root = objectMapper.readTree(q.getQuestion());
            JsonNode secondaryObjectiveNode = root.get("secondaryObjectives");
            if(secondaryObjectiveNode != null && secondaryObjectiveNode.isArray()){
                List<JsonNode> filtered = new java.util.ArrayList<>();
                for (JsonNode obj : secondaryObjectiveNode) {
                    if (obj.path("id").asInt() != idSecondaryObj) {
                        filtered.add(obj);
                    }
                }
                ((ObjectNode) root).putArray("secondaryObjectives").addAll(filtered);
                q.setQuestion(objectMapper.writeValueAsString(root));
            }
        }catch(Exception e){
            throw new RuntimeException(e.getMessage());
        }
        return questionRepository.save(q);
    }


    public Question addCode(String idQuestion, String newCode) throws IOException {
        Question question = questionRepository.findByQuestionId_IdQuestion(idQuestion)
                .orElseThrow(() -> new RuntimeException("Question not found with id: " + idQuestion));

        JsonNode root = objectMapper.readTree(question.getQuestion());
        ArrayNode contentArray;
        if (root.has("Content") && root.get("Content").isArray()) {
            contentArray = (ArrayNode) root.get("Content");
        } else {
            contentArray = objectMapper.createArrayNode();
            ((ObjectNode) root).set("Content", contentArray);
        }

        if (contentArray.size() > 0) {
            ObjectNode lastContent = (ObjectNode) contentArray.get(contentArray.size() - 1);
            String existingCode = lastContent.has("cod") ? lastContent.get("cod").asText() : "";
            lastContent.put("cod", existingCode.isEmpty() ? newCode : existingCode + "\n" + newCode);
        } else {
            ObjectNode newContent = objectMapper.createObjectNode();
            newContent.put("textUP", (String) null);
            newContent.put("cod", newCode);
            contentArray.add(newContent);
        }

        String updatedJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(root);
        question.setQuestion(updatedJson);

        return questionRepository.save(question);
    }

    public Question addTextToContent(String idQuestion, String newText) throws IOException {
        Question question = questionRepository.findByQuestionId_IdQuestion(idQuestion)
                .orElseThrow(() -> new RuntimeException("Question not found with id: " + idQuestion));

        JsonNode root = objectMapper.readTree(question.getQuestion());
        ArrayNode contentArray;
        if (root.has("Content") && root.get("Content").isArray()) {
            contentArray = (ArrayNode) root.get("Content");
        } else {
            contentArray = objectMapper.createArrayNode();
            ((ObjectNode) root).set("Content", contentArray);
        }

        ObjectNode firstContent;
        if (contentArray.size() > 0) {
            firstContent = (ObjectNode) contentArray.get(0);
        } else {
            firstContent = objectMapper.createObjectNode();
            contentArray.add(firstContent);
        }
        firstContent.put("textUP", newText);
        String existingCode = firstContent.has("cod") ? firstContent.get("cod").asText() : "";
        firstContent.put("cod", existingCode);
        String updatedJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(root);
        question.setQuestion(updatedJson);

        return questionRepository.save(question);
    }

    public Question addResponse(String idQuestion, String response){
        Question q = questionRepository.findByQuestionId_IdQuestion(idQuestion)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        try {
            JsonNode root = objectMapper.readTree(q.getQuestion());
            JsonNode secondaryObjectiveNode = root.get("responses");
            if (secondaryObjectiveNode == null || !secondaryObjectiveNode.isArray()) {
                ((ObjectNode) root).putArray("responses");
                secondaryObjectiveNode = root.get("responses");
            }
            ObjectNode newObjective = objectMapper.createObjectNode();
            newObjective.put("response", response);
            ((com.fasterxml.jackson.databind.node.ArrayNode) secondaryObjectiveNode).add(newObjective);
            q.setQuestion(objectMapper.writeValueAsString(root));
        }catch (Exception e){
            new RuntimeException(e.getMessage());
        }

        return questionRepository.save(q);
    }

    public Question addAnswer(String idQuestion, String answer){
        Question q = questionRepository.findByQuestionId_IdQuestion(idQuestion)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        try {
            JsonNode root = objectMapper.readTree(q.getQuestion());
            JsonNode answerNode = root.get("answers");
            if (answerNode == null || !answerNode.isArray()) {
                ((ObjectNode) root).putArray("answers");
                answerNode= root.get("answers");
            }
            ObjectNode newObjective = objectMapper.createObjectNode();
            newObjective.put("answer", answer);
            ((com.fasterxml.jackson.databind.node.ArrayNode)answerNode).add(newObjective);
            q.setQuestion(objectMapper.writeValueAsString(root));
        }catch (Exception e){
            new RuntimeException(e.getMessage());
        }

        return questionRepository.save(q);
    }

    public Question deleteResponse(String idQuestion, String response){
        Question q = questionRepository.findByQuestionId_IdQuestion(idQuestion)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        try{
            JsonNode root = objectMapper.readTree(q.getQuestion());
            JsonNode reesponsesNode = root.get("responses");
            if(reesponsesNode!= null && reesponsesNode.isArray()){
                List<JsonNode> filtered = new java.util.ArrayList<>();
                for (JsonNode obj : reesponsesNode) {
                    if (!obj.path("respoonse").asText().trim().equals(response.trim())) {
                        filtered.add(obj);
                    }
                }
                ((ObjectNode) root).putArray("responses").addAll(filtered);
                q.setQuestion(objectMapper.writeValueAsString(root));
            }
        }catch(Exception e){
            throw new RuntimeException(e.getMessage());
        }
        return questionRepository.save(q);
    }

    public Question deleteAnswer(String idQuestion, String answer){
        Question q = questionRepository.findByQuestionId_IdQuestion(idQuestion)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        try{
            JsonNode root = objectMapper.readTree(q.getQuestion());
            JsonNode answerNode = root.get("answers");
            if(answerNode != null && answerNode.isArray()){
                List<JsonNode> filtered = new java.util.ArrayList<>();
                for (JsonNode obj : answerNode) {
                    if (!obj.path("answer").asText().trim().equals(answer.trim())) {
                        filtered.add(obj);
                    }
                }
                ((ObjectNode) root).putArray("answers").addAll(filtered);
                q.setQuestion(objectMapper.writeValueAsString(root));
            }
        }catch(Exception e){
            throw new RuntimeException(e.getMessage());
        }
        return questionRepository.save(q);
    }

    private Set<Integer> getMainObjectivesForQuestion(String idQuestion) {
        return mainObjectiveRepository.findByIdQuestion(idQuestion)
                .stream()
                .map(MainObjective::getIdObjectiv)
                .collect(Collectors.toSet());
    }

    private Set<Integer> getSubObjectivesForQuestion(String idQuestion) {
        return secondaryObjectiveRepository.findByIdQuestion(idQuestion)
                .stream()
                .map(SecondaryObjective::getIdObjectiv)
                .collect(Collectors.toSet());
    }

    private Set<String> getLabels(String idQuestion){
        return questionLabelsRepository.findByIdQuestion(idQuestion)
                .stream()
                .map(QuestionLabels::getLabel)
                .collect(Collectors.toSet());
    }

    private Set<Integer> getDifficulty(String idQuestion){
        return timeLengthQuestionRepository.findByIdQuestion(idQuestion)
                .stream()
                .map(TimeLengthQuestion::getTimeLength)
                .collect(Collectors.toSet());
    }

    public Metric distance(Question q1, Question q2) {
        if (q1 == null || q2 == null) {
            System.err.println("One of the questions is null!");
            return null;
        }

        Metric metric = new Metric();

        if (!q1.getDomain().equals(q2.getDomain())) {
           metric.setScoreDifficulty(0.0);
           metric.setScoreObjectives(0.0);
           metric.setScoreGuiType(0.0);
           metric.setScoreLabel(0.0);
           metric.setScoreSubObjectives(0.0);
        }

        if(q1.getQuestionId().getId() == q2.getQuestionId().getId() && q1.getQuestionId().getIdQuestion().equals(q2.getQuestionId().getIdQuestion())){
            metric.setScoreDifficulty(1.0);
            metric.setScoreObjectives(1.0);
            metric.setScoreGuiType(1.0);
            metric.setScoreLabel(1.0);
            metric.setScoreSubObjectives(1.0);
        }


        Set<Integer> idQuest1 = getMainObjectivesForQuestion(q1.getQuestionId().getIdQuestion());
        Set<Integer> idQuest2 = getMainObjectivesForQuestion(q2.getQuestionId().getIdQuestion());

        Set<Integer> idSubQuest1 = getSubObjectivesForQuestion(q1.getQuestionId().getIdQuestion());
        Set<Integer> idSubQuest2 = getSubObjectivesForQuestion(q2.getQuestionId().getIdQuestion());

        Set<String> labelQuest1 = getLabels(q1.getQuestionId().getIdQuestion());
        Set<String> labelQuest2 = getLabels(q2.getQuestionId().getIdQuestion());

        if (idQuest1.equals(idQuest2)) {
            if (!labelQuest1.equals(labelQuest2)) {
                double sum = 0;
                for (Integer i : idSubQuest1) {
                    for (Integer j : idSubQuest2) {
                        sum += 0.1;
                    }
                }
                if (sum != 0) {
                    metric.setScoreObjectives(sum);
                }
            }
        }


        if (idQuest1.equals(idQuest2) && idSubQuest1.equals(idSubQuest2)) {
            double sum = labelQuest1.stream()
                    .filter(labelQuest2::contains)
                    .count() * 0.1;
            metric.setScoreLabel(sum);
        }

        Set<Integer> difficultyQuest1 = getDifficulty(q1.getQuestionId().getIdQuestion());
        Set<Integer> difficultyQuest2 = getDifficulty(q2.getQuestionId().getIdQuestion());

        if (difficultyQuest1.equals(difficultyQuest2)) {
            metric.setScoreDifficulty(0.2);
        }

        return metric;
    }


    public Optional<Question> findById(QuestionId id) {
        return questionRepository.findById(id);
    }

}
