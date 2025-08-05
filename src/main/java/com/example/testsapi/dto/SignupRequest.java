package com.example.testsapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class SignupRequest {
    @NotBlank
    @Size(min = 3, max = 20)
    private String username;

    @NotBlank
    @Size(max = 50)
    private String firstName;

    @NotBlank
    @Size(max = 200)
    private String secondName;

    private LocalDate dataBirth;

    @NotBlank
    @Size(max = 20)
    private String phoneNumber;

    @NotBlank
    @Size(max = 100)
    @Email
    private String email;

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;

    private String status;
    private String role;

    // Getters și Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getSecondName() { return secondName; }
    public void setSecondName(String secondName) { this.secondName = secondName; }

    public LocalDate getDataBirth() { return dataBirth; }
    public void setDataBirth(LocalDate dataBirth) { this.dataBirth = dataBirth; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getStatus() {
        return status;
    }

    public String getRole() {
        return role;
    }
}