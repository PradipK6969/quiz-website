package com.example.quiz.model;

import java.sql.Timestamp;
import java.util.List;

public class Quiz {
    private int id;
    private String title;
    private String description;
    private String category;
    private int createdByAdminId;
    private Timestamp createdAt;
    private List<Question> questions;
    private int durationMinutes; // Duration in minutes, 0 or null might mean no timer

    public Quiz() {}

    // Constructor for basic details
    public Quiz(int id, String title, String description, String category, int createdByAdminId, Timestamp createdAt, int durationMinutes) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.createdByAdminId = createdByAdminId;
        this.createdAt = createdAt;
        this.durationMinutes = durationMinutes;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getCreatedByAdminId() {
        return createdByAdminId;
    }

    public void setCreatedByAdminId(int createdByAdminId) {
        this.createdByAdminId = createdByAdminId;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }
}