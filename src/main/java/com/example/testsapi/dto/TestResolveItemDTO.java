package com.example.testsapi.dto;

import java.util.List;

public class TestResolveItemDTO {
    private String idUser;
    private String idTest;
    private String idQuestion;
    private List<String> selectedAnswers;

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getIdTest() {
        return idTest;
    }

    public void setIdTest(String idTest) {
        this.idTest = idTest;
    }

    public String getIdQuestion() {
        return idQuestion;
    }

    public void setIdQuestion(String idQuestion) {
        this.idQuestion = idQuestion;
    }

    public List<String> getSelectedAnswers() {
        return selectedAnswers;
    }
}
