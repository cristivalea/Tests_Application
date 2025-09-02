package com.example.testsapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "response")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Response {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idResponse", nullable = false, unique = true)
    private Integer id;

    @Column(name = "type", nullable = false, length = 100)
    private String type;

    @Column(name = "content", nullable = false, length = 200)
    private String content;

    @Column(name = "selectedType", nullable = false, length = 100)
    private String selectedType;

    @Column(name = "responseValue", nullable = false, length = 1)
    private int responseValue;
}
