package com.example.testsapi.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SecondaryObjectiveId implements Serializable {
    private Integer id;
    private String idQuestion;
}
