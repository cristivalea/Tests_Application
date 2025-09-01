package com.example.testsapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "time_length_question")
@IdClass(TimeLengthQuestionId.class)
public class TimeLengthQuestion {

    @Id
    @Column(name = "id")
    private Integer id;

    @Id
    @Column(name = "idQuestion", nullable = false, length = 100)
    private String idQuestion;

    @Column(name = "timeLength", nullable = false)
    private Integer timeLength;
}
