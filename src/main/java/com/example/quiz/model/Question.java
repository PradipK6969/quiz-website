package com.example.quiz.model;

import java.util.List;

public class Question {
    private int id;
    private int quizId;
    private String questionText;
    private String questionType; // e.g., "MCQ"
    private List<Option> options; // For MCQ type

    public Question() {}

    public Question(int id, int quizId, String questionText, String questionType) {
        this.id = id;
        this.quizId = quizId;
        this.questionText = questionText;
        this.questionType = questionType;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuizId() {
        return quizId;
    }

    public void setQuizId(int quizId) {
        this.quizId = quizId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }
}
