package com.example.testsapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class EmailRequest {

    @NotBlank(message = "Email address must not be blank")
    @Email(message = "Invalid email format")
    private String address;

    @NotBlank(message = "UserId must not be blank")
    private String userId;

    public EmailRequest() {}

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
