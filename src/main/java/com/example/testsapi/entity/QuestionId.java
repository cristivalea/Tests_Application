package com.example.testsapi.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class QuestionId implements Serializable {
    private Integer id;
    private String idQuestion;

    public QuestionId() {}

    public QuestionId(Integer id, String idQuestion) {
        this.id = id;
        this.idQuestion = idQuestion;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getIdQuestion() { return idQuestion; }
    public void setIdQuestion(String idQuestion) { this.idQuestion = idQuestion; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QuestionId)) return false;
        QuestionId that = (QuestionId) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(idQuestion, that.idQuestion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, idQuestion);
    }
}
