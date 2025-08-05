package com.example.testsapi.entity;

public enum Status {

    INACTIV("INACTIV"),
    ACTIVE("ACTIV");

    private String name;

    private Status(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
