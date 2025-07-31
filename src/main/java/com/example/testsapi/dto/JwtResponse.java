package com.example.testsapi.dto;

import java.time.LocalDate;
import java.util.List;

public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private String idUser;
    private String username;
    private String firstName;
    private String secondName;
    private List<String> roles;

    public JwtResponse(String accessToken, String idUser, String username,
                       String firstName, String secondName, List<String> roles) {
        this.token = accessToken;
        this.idUser = idUser;
        this.username = username;
        this.firstName = firstName;
        this.secondName = secondName;
        this.roles = roles;
    }

    // Getters și Setters
    public String getAccessToken() { return token; }
    public void setAccessToken(String accessToken) { this.token = accessToken; }

    public String getTokenType() { return type; }
    public void setTokenType(String tokenType) { this.type = tokenType; }

    public String getIdUser() { return idUser; }
    public void setIdUser(String idUser) { this.idUser = idUser; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getSecondName() { return secondName; }
    public void setSecondName(String secondName) { this.secondName = secondName; }

    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }
}