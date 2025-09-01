package com.example.testsapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "secondary_objectives")
@IdClass(SecondaryObjectiveId.class)
public class SecondaryObjective {
    @Id
    @Column(name = "id")
    private Integer id;

    @Id
    @Column(name = "idQuestion", nullable = false, length = 100)
    private String idQuestion;

    @Column(name = "idObjective", nullable = false)
    private Integer idObjectiv;

    @Column(name = "nameObjective", nullable = false, length = 10000)
    private String nameObjectiv;
}
