package com.example.testsapi.dto;


import java.util.List;
import java.util.Map;

public class QuestionPayload {
    private String format;
    private String type;
    private String id;
    private List<Map<String, Object>> mainObjectives;
    private List<Map<String, Object>> secondaryObjectives;
    private int estimateSeconds;
    private Map<String, Object> Content;
    private List<Map<String, Object>> responses;
    private List<Map<String, Object>> answers;
    private String hint;

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Map<String, Object>> getMainObjectives() {
        return mainObjectives;
    }

    public void setMainObjectives(List<Map<String, Object>> mainObjectives) {
        this.mainObjectives = mainObjectives;
    }

    public List<Map<String, Object>> getSecondaryObjectives() {
        return secondaryObjectives;
    }

    public void setSecondaryObjectives(List<Map<String, Object>> secondaryObjectives) {
        this.secondaryObjectives = secondaryObjectives;
    }

    public int getEstimateSeconds() {
        return estimateSeconds;
    }

    public void setEstimateSeconds(int estimateSeconds) {
        this.estimateSeconds = estimateSeconds;
    }

    public Map<String, Object> getContent() {
        return Content;
    }

    public void setContent(Map<String, Object> content) {
        Content = content;
    }

    public List<Map<String, Object>> getResponses() {
        return responses;
    }

    public void setResponses(List<Map<String, Object>> responses) {
        this.responses = responses;
    }

    public List<Map<String, Object>> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Map<String, Object>> answers) {
        this.answers = answers;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }
}
