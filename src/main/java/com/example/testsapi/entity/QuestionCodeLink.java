package com.example.testsapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "question_code_link")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionCodeLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Integer id;

    @Column(name = "external_id", nullable = false, length = 100)
    private String externalId;

    @Column(name = "sourceCodeId", nullable = false)
    private int sourceCodeId;

    @Column(name = "type", nullable = false, length = 100)
    private String type;
}
