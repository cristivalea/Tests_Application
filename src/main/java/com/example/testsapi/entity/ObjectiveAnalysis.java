package com.example.testsapi.entity;

import lombok.*;
import java.util.Map;

@Data
@AllArgsConstructor
public class ObjectiveAnalysis {
    private String obiectiv;
    private String grup;
    private double mediePunctaj;
    private double medieTimp;
    private String statusDificultate;
}