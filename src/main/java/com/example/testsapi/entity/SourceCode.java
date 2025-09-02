package com.example.testsapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "source_code")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SourceCode {

    @Id
    @Column(name = "idSourceCode", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idSourceCode;

    @Column(name = "programmingLanguage", nullable = false, length = 100)
    private String programmingLanguage;

    @Column(name = "description", nullable = false, length = 5000)
    private String description;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "fileName", nullable = false, length = 500)
    private String fileName;
}
