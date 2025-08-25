package com.example.testsapi.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "question")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50)
    private String idQuestion;

    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String question;

    public Question(Integer id, String idQuestion, String question) {
        this.id = id;
        this.idQuestion = idQuestion;
        this.question = question;
    }

    public Question(){}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIdQuestion() {
        return idQuestion;
    }

    public void setIdQuestion(String idQuestion) {
        this.idQuestion = idQuestion;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}
