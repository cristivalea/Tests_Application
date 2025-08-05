package com.example.testsapi.dto;

import com.example.testsapi.entity.Status;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.sql.Statement;

public class SecurityQuestionRequest {
    @NotBlank(message = "security question must not be blank")
    private String sec_quest;

    @NotBlank(message = "Response must not be blank")
    private String response;

    @NotBlank(message = "UserId must not be blank")
    private String userId;

    private Status status;

    public SecurityQuestionRequest(){}

    public String getSec_quest() {
        return sec_quest;
    }

    public void setSec_quest(String sec_quest) {
        this.sec_quest = sec_quest;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
