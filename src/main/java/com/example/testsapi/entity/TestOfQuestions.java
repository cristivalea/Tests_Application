package com.example.testsapi.entity;

import java.util.List;

public class TestOfQuestions {
    private Test test;
    private List<Question> quesyions;

    public TestOfQuestions(Test test, List<Question> quesyions) {
        this.test = test;
        this.quesyions = quesyions;
    }

    public Test getTest() {
        return test;
    }

    public void setTest(Test test) {
        this.test = test;
    }

    public List<Question> getQuesyions() {
        return quesyions;
    }

    public void setQuesyions(List<Question> quesyions) {
        this.quesyions = quesyions;
    }
}
