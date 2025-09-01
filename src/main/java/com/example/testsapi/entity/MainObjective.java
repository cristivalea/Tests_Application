package com.example.testsapi.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "main_objectives")
@IdClass(MainObjectiveId.class)
public class MainObjective {

    @Id
    @Column(name = "id")
    private Integer id;

    @Id
    @Column(name = "idQuestion", nullable = false, length = 100)
    private String idQuestion;

    @Column(name = "idObjectiv", nullable = false)
    private Integer idObjectiv;

    @Column(name = "nameObjectiv", nullable = false, length = 10000)
    private String nameObjectiv;
}