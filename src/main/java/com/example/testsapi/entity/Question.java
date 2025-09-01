package com.example.testsapi.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "question")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Question {

    @EmbeddedId
    private QuestionId questionId;

    @Lob
    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String question;
}
