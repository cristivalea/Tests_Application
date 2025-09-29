package com.example.testsapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "question_labels")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionLabels {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Integer id;

    @Column(name = "idQuestion", length = 100)
    private String idQuestion;

    @Column(name = "label", length = 200)
    private String label;
}
