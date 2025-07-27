package com.example.quiz.model;

import java.sql.Timestamp;

public class LeaderboardEntry {
    private String username;
    private String quizTitle;
    private int score;
    private int totalQuestions; // Useful to show score like "8/10"
    private Timestamp completedAt;

    public LeaderboardEntry() {}

    public LeaderboardEntry(String username, String quizTitle, int score, int totalQuestions, Timestamp completedAt) {
        this.username = username;
        this.quizTitle = quizTitle;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.completedAt = completedAt;
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getQuizTitle() {
        return quizTitle;
    }

    public void setQuizTitle(String quizTitle) {
        this.quizTitle = quizTitle;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Timestamp getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Timestamp completedAt) {
        this.completedAt = completedAt;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }
}