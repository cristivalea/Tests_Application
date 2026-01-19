package com.example.testsapi.dto;

public class TestResolveResultDTO {

    private int totalQuestions;
    private int correctQuestions;
    private double percentage;

    public TestResolveResultDTO(int totalQuestions,
                                int correctQuestions,
                                double percentage) {
        this.totalQuestions = totalQuestions;
        this.correctQuestions = correctQuestions;
        this.percentage = percentage;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public int getCorrectQuestions() {
        return correctQuestions;
    }

    public double getPercentage() {
        return percentage;
    }
}