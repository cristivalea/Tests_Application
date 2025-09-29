package com.example.testsapi.entity;

public class Metric {
    private double scoreObjectives;
    private double scoreSubObjectives;
    private double scoreLabel;
    private double scoreDifficulty;
    private double scoreGuiType;

    public Metric(double scoreObjectives, double scoreSubObjectives, double scoreLabel, double scoreDifficulty, double scoreGuiType) {
        this.scoreObjectives = scoreObjectives;
        this.scoreSubObjectives = scoreSubObjectives;
        this.scoreLabel = scoreLabel;
        this.scoreDifficulty = scoreDifficulty;
        this.scoreGuiType = scoreGuiType;
    }

    public Metric(){
        this.scoreObjectives = 0;
        this.scoreSubObjectives = 0;
        this.scoreLabel = 0;
        this.scoreDifficulty = 0;
        this.scoreGuiType = 0;
    }

    public double getScoreObjectives() {
        return scoreObjectives;
    }

    public void setScoreObjectives(double scoreObjectives) {
        this.scoreObjectives = scoreObjectives;
    }

    public double getScoreSubObjectives() {
        return scoreSubObjectives;
    }

    public void setScoreSubObjectives(double scoreSubObjectives) {
        this.scoreSubObjectives = scoreSubObjectives;
    }

    public double getScoreLabel() {
        return scoreLabel;
    }

    public void setScoreLabel(double scoreLabel) {
        this.scoreLabel = scoreLabel;
    }

    public double getScoreDifficulty() {
        return scoreDifficulty;
    }

    public void setScoreDifficulty(double scoreDifficulty) {
        this.scoreDifficulty = scoreDifficulty;
    }

    public double getScoreGuiType() {
        return scoreGuiType;
    }

    public void setScoreGuiType(double scoreGuiType) {
        this.scoreGuiType = scoreGuiType;
    }

    public double getSum(){
        return this.scoreObjectives + this.scoreSubObjectives + this.scoreLabel + this.scoreDifficulty + this.scoreGuiType;
    }
}
