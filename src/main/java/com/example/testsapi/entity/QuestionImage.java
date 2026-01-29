package com.example.testsapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.awt.*;

@Entity
@Table(name = "question_image")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Integer id;

    @Column(name = "idQuestion", length = 100)
    private String idQuestion;

    @Column(name = "imagePath", length = 255)
    private String imagePath;

}
