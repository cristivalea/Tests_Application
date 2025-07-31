package com.example.testsapi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "security_questions")
public class SecurityQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")  // coloana corectă în DB
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idUser", referencedColumnName = "idUser")
    private User user;

    @NotBlank
    @Size(max = 500)
    @Column(name = "question")
    private String question;

    @NotBlank
    @Size(max = 500)
    @Column(name = "response")
    private String response;

    // Constructori
    public SecurityQuestion() {}

    public SecurityQuestion(User user, String question, String response) {
        this.user = user;
        this.question = question;
        this.response = response;
    }

    // Getters și Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public String getResponse() { return response; }
    public void setResponse(String response) { this.response = response; }
}