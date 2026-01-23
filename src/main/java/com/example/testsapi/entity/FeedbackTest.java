package com.example.testsapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "feedback_test")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Integer id;

    @Column(name = "idUser", length = 50)
    private String idUser;

    @Column(name = "idTest", length = 100)
    private String idTest;

    @Column(name = "relevance")
    private boolean relevance;

    @Column(name = "score")
    private int score;
}
