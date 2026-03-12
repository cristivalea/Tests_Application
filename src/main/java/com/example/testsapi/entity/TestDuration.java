package com.example.testsapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "test_duration")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TestDuration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "difficulty", length = 50)
    private String difficulty;

    @Column(name = "finishTime")
    private String finishTime;

    @Column(name = "timeTakenSeconds")
    private int timeTakenSeconds;

    @Column(name = "idTest", length = 50)
    private String idTest;

    @Column(name = "idUser", length = 6)
    private String idUser;
}
