package com.example.testsapi.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "main_objectives")
public class MainObjective {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "idQuestion", nullable = false, length = 100)
    private String idQuestion;

    @Column(name = "idObjectiv", nullable = false)
    private Integer idObjectiv;

    @Column(name = "nameObjectiv", nullable = false, length = 10000)
    private String nameObjectiv;
}