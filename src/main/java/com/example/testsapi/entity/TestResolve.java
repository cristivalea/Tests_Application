package com.example.testsapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "rezolvare_test")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TestResolve {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "idUser", length = 6)
    private String idUser;

    @Column(name = "idTest", length = 50)
    private String idTest;

    @Column(name = "idQuestion", length = 10)
    private String idQuestion;

    @Column(name = "response")
    private boolean response;


}
