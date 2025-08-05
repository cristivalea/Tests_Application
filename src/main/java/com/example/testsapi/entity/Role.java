package com.example.testsapi.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum Role {
    USER("USER", Arrays.asList("READ_PROFILE", "EDIT_PROFILE")),
    ADMIN("ADMIN", Arrays.asList("READ_PROFILE", "EDIT_PROFILE", "DELETE_USER", "MANAGE_SYSTEM"));
    private String name;
    private List<String> operations;

    private Role(String name, List<String> operations){
        this.name = name;
        this.operations = operations;
    }

    public String getName() {
        return name;
    }

    public List<String> getOperations() {
        return operations;
    }
}
