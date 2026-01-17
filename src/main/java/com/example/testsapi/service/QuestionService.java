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
    @Autowired
    private ResponseRepository responseRepository;
    @Autowired
    private ResponsesRepository responsesRepository;
    @Autowired
    private SourceCodeRepository sourceCodeRepository;
    @Autowired
    private QuestionCodeLinkRepository questionCodeLinkRepository;

    public Question saveQuestion(String json) throws Exception {

        JsonNode root = objectMapper.readTree(json);
        String idQuestion = root.get("id").asText();
        String level = root.get("type").asText();
        String score = root.get("partialScore").asText();
        String domain = root.get("domain").asText();

        int partialScore  = 0;
        if(score.equals("true")){
            partialScore = 1;
        } else if(score.equals("false")){
            partialScore = 0;
        }

        Integer nextId = (int) (questionRepository.count() + 1);

        Question question = new Question();
        question.setQuestionId(new QuestionId(nextId, idQuestion));
        question.setQuestion(json);
        question.setLevel(level);
        question.setScore(partialScore);
        question.setDomain(domain);
        questionRepository.save(question);

        //add in main_objectivs table
        JsonNode mainObjectivesNode = root.get("mainObjectives");

        if (mainObjectivesNode != null && mainObjectivesNode.isArray()) {

            for (JsonNode obj : mainObjectivesNode) {

                Integer idObj = obj.get("id").asInt();
                String nameObj = obj.get("name").asText();

                MainObjective mo = new MainObjective();
                mo.setIdQuestion(idQuestion);
                mo.setIdObjectiv(idObj);
                mo.setNameObjectiv(nameObj);

                mainObjectiveRepository.save(mo);
            }
        }

        //add secondary objectives table
        JsonNode secondaryObjectivesNode = root.get("secondaryObjectives");

        if (secondaryObjectivesNode != null && secondaryObjectivesNode.isArray()) {

            for (JsonNode obj : secondaryObjectivesNode) {

                Integer idObj = obj.get("id").asInt();
                String nameObj = obj.get("name").asText();

                SecondaryObjective so = new SecondaryObjective();
                so.setIdQuestion(idQuestion);
                so.setIdObjectiv(idObj);
                so.setNameObjectiv(nameObj);

                secondaryObjectiveRepository.save(so);
            }
        }

        //time lenght
        TimeLengthQuestion timeLengthQuestion = new TimeLengthQuestion();
        Integer time = null;
        if(level.equals("SOFTQUESTION")){
            time = 60;
        } else if(level.equals("MIDLELEVELQUESTION")){
            time = 100;
        } else if(level.equals("HARDQUESTION")){
            time = 150;
        }
        timeLengthQuestion.setIdQuestion(idQuestion);
        timeLengthQuestion.setTimeLength(time);
        timeLengthQuestionRepository.save(timeLengthQuestion);

        JsonNode responsesNode = root.get("responses");
        JsonNode answersNode = root.get("answers");

        List<Integer> correctAnswersIndexes = new ArrayList<>();

        // adăugăm toate răspunsurile corecte
        if (answersNode != null && answersNode.isArray()) {
            for (int j = 0; j < answersNode.size(); j++) {
                if (answersNode.get(j).get("answer").asBoolean()) {
                    correctAnswersIndexes.add(j);
                }
            }
        }

        // Salvezi răspunsurile
        for (int i = 0; i < responsesNode.size(); i++) {
            String responseText = responsesNode.get(i).get("respoonse").asText();
            int isCorrect = correctAnswersIndexes.contains(i) ? 1 : 0;

            // Salvezi în tabela RESPONSE
            Response response = new Response();
            if(correctAnswersIndexes.size() == 1){
                response.setType("single_choice");
            } else if(correctAnswersIndexes.size() > 1){
                response.setType("multiple_choice");
            }
            response.setContent(responseText);
            response.setSelectedType("text");
            response.setResponseValue(isCorrect);

            Response saved = responseRepository.save(response);

            // Salvezi legătura între întrebare și răspuns în tabela RESPONSES
            Responses link = new Responses();
            link.setIdQuestion(idQuestion);
            link.setIdResponse(saved.getId());

            responsesRepository.save(link);
        }

        JsonNode contentNode = root.get("content");

        if (contentNode != null) {
            JsonNode codeNode = contentNode.get("sourceCode");

            if (codeNode != null && !codeNode.isNull()) {
                System.err.println(codeNode.asText());

                SourceCode sourceCode = new SourceCode();
                sourceCode.setProgrammingLanguage("Java");
                sourceCode.setDescription("Code for indexing books using StringBuilder");
                sourceCode.setContent(codeNode.asText());
                sourceCode.setFileName("Indexing.java");

                sourceCodeRepository.save(sourceCode);

                QuestionCodeLink codeLink = new QuestionCodeLink();
                codeLink.setExternalId(idQuestion);
                codeLink.setSourceCodeId(sourceCode.getIdSourceCode());
                codeLink.setType("source_code");

                questionCodeLinkRepository.save(codeLink);
            }
        }


        return null;
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

        // domeniul diferit
        if (!q1.getDomain().equals(q2.getDomain())) {
            metric.setScoreDifficulty(0.0);
            metric.setScoreObjectives(0.0);
            metric.setScoreGuiType(0.0);
            metric.setScoreLabel(0.0);
            metric.setScoreSubObjectives(0.0);
            return metric;
        }

        // id și idQuestion
        if (q1.getQuestionId().getId().equals(q2.getQuestionId().getId()) &&
                q1.getQuestionId().getIdQuestion().equals(q2.getQuestionId().getIdQuestion())) {
            metric.setScoreDifficulty(1.0);
            metric.setScoreObjectives(1.0);
            metric.setScoreGuiType(1.0);
            metric.setScoreLabel(1.0);
            metric.setScoreSubObjectives(1.0);
            return metric;
        }

        Set<Integer> idQuest1 = getMainObjectivesForQuestion(q1.getQuestionId().getIdQuestion());
        Set<Integer> idQuest2 = getMainObjectivesForQuestion(q2.getQuestionId().getIdQuestion());

        Set<Integer> idSubQuest1 = getSubObjectivesForQuestion(q1.getQuestionId().getIdQuestion());
        Set<Integer> idSubQuest2 = getSubObjectivesForQuestion(q2.getQuestionId().getIdQuestion());

        Set<String> labelQuest1 = getLabels(q1.getQuestionId().getIdQuestion());
        Set<String> labelQuest2 = getLabels(q2.getQuestionId().getIdQuestion());

        // scor pentru obiective principale
        if (idQuest1.equals(idQuest2)) {
            metric.setScoreObjectives(1.0);
        }

        // scor pentru sub-obiective
        if (idSubQuest1.equals(idSubQuest2)) {
            metric.setScoreSubObjectives(1.0);
        } else {
            double sum = 0.0;
            for (Integer i : idSubQuest1) {
                if (idSubQuest2.contains(i)) {
                    sum += 0.1;
                }
            }
            if (sum > 0) {
                metric.setScoreSubObjectives(sum);
            }
        }

        // scor pentru etichete
        if (labelQuest1.equals(labelQuest2)) {
            metric.setScoreLabel(1.0);
        } else {
            double sum = 0.0;
            for (String l1 : labelQuest1) {
                if (labelQuest2.contains(l1)) {
                    sum += 0.1;
                }
            }
            if (sum > 0) {
                metric.setScoreLabel(sum);
            }
        }

        // scor pentru dificultate
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
