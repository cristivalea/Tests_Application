package com.example.testsapi.entity;

public enum ResponseType {
    SINGLE_CHOICE("SINGLE_CHOICE", 1),
    MULTIPLE_CHOICE("MULTIPLE_CHOICE", 2);

    private String denumire;
    private int id;
    private ResponseType(String denumire, int id){
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
