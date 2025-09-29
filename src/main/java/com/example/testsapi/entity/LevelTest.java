package com.example.testsapi.entity;

public enum LevelTest {
    BEGINNER("BEGINNER", 1),
    INTERMEDIATE("INTERMEDIATE", 2),
    HARD("HARD", 3);
    private String denumire;
    private int id;

    private LevelTest(String denumire, int id){
        this.denumire = denumire;
        this.id = id;
    }

    public String getDenumire() {
        return denumire;
    }

    public int getId() {
        return id;
    }
}
