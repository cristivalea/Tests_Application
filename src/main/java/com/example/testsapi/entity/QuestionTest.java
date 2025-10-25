package com.example.testsapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "question_test")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionTest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "idTest", length = 50)
    private String idTest;

    @Column(name = "idQuestion", length = 100)
    private String idQuestion;
}
