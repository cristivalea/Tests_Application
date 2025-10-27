package com.example.testsapi;

import com.example.testsapi.entity.Question;
import com.example.testsapi.entity.QuestionId;
import com.example.testsapi.service.TestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestServiceDistanceMinimalTest {

    private TestService testService;

    @BeforeEach
    void setUp() {
        testService = new TestService();
    }

    private Question createQuestion(String id, String level, String gui) {
        Question q = new Question();
        QuestionId qId = new QuestionId();
        qId.setIdQuestion(id);
        q.setQuestionId(qId);
        q.setLevel(level);
        q.setQuestion("{\"format\":\"" + gui + "\"}");
        return q;
    }

    @Test
    void testDistanceSameId() {
        Question q1 = createQuestion("Q1", "MIDLELEVELQUESTION", "TEXT_UP_COD");
        Question q2 = createQuestion("Q1", "HARDQUESTION", "TEXT_UP_COD");

        double distance = testService.distanceToQuestion(q1, q2);

        // Dacă ID-urile sunt identice, distance trebuie să fie 1.0
        assertEquals(1.0, distance);
    }

    @Test
    void testDistanceDifferentGui() {
        Question q1 = createQuestion("Q1", "MIDLELEVELQUESTION", "TEXT_UP_COD");
        Question q2 = createQuestion("Q2", "MIDLELEVELQUESTION", "TEXT_DOWN_COD");

        double distance = testService.distanceToQuestion(q1, q2);

        // Nivelul este același (0.2), GUI diferit (0.3)
        assertEquals(0.5, distance, 0.01);
    }

    @Test
    void testDistanceDifferentLevelAndGui() {
        Question q1 = createQuestion("Q1", "MIDLELEVELQUESTION", "TEXT_UP_COD");
        Question q2 = createQuestion("Q2", "HARDQUESTION", "TEXT_DOWN_COD");

        double distance = testService.distanceToQuestion(q1, q2);

        // Nivel diferit, GUI diferit (0.3)
        assertEquals(0.3, distance, 0.01);
    }
}
