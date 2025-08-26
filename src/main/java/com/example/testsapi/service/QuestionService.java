package com.example.testsapi.service;

import com.example.testsapi.entity.Question;
import com.example.testsapi.entity.QuestionId;
import com.example.testsapi.repository.QuestionRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Hashtable;
import java.util.Map;
import java.util.Optional;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

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
            throw new RuntimeException("Întrebarea nu a fost găsită!");
        }

        // Luăm JSON-ul brut din DB
        String rawJson = optionalQuestion.get().getQuestion();

        // Parsăm JSON-ul
        JsonNode root = objectMapper.readTree(rawJson);

        // Accesăm primul element din Content (care este un array)
        JsonNode contentArray = root.path("Content");
        if (contentArray.isArray() && contentArray.size() > 0) {
            JsonNode firstContent = contentArray.get(0);

            // Extragem codul original
            String originalCode = firstContent.path("cod").asText();

            String formattedCode = originalCode;

            // Suprascrii valoarea codului în JSON
            if (firstContent.isObject()) {
                ((ObjectNode) firstContent).put("cod", formattedCode);
            }
        }

        // Returnezi JSON-ul frumos indentat
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(root);
    }

}
