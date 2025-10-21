package com.example.testsapi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "test")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Test {
    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "idTest", length = 50)
    private String idTest;

    @Column(name = "nrOfObjectives")
    private int nrOfObjectives;

    @Column(name = "level", length = 100)
    private LevelTest level;

    @Column(name = "nrOfQuestions")
    private int nrOfQuestions;

    @Column(name = "estimatedDuration")
    private int estimatedDuration;
}
