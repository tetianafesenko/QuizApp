package com.example.quizapp;

import java.io.Serializable;

public class QuizResult implements Serializable {
    private String date;
    private int score;

    public QuizResult(String date, int score) {
        this.date = date;
        this.score = score;
    }

}