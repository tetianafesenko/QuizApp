package com.example.quizapp;

import java.util.ArrayList;
import java.util.List;


public class QuizData {
    public static List<Question> questions;

    static {
        questions = new ArrayList<>();
        // Add sample questions to the list
        questions.add(new Question("Is the sky blue?", true));
        questions.add(new Question("Water boils at 100 degrees Celsius?", true));
        questions.add(new Question("Android is a programming language?", false));
        // Add more questions as needed
    }

    public static List<Question> getQuestions() {
        return questions;
    }

    // Question class representing each quiz question
    public static class Question {
        private String text;
        private boolean answer;

        public Question(String text, boolean answer) {
            this.text = text;
            this.answer = answer;
        }

        public String getText() {
            return text;
        }

        public boolean getAnswer() {
            return answer;
        }
    }
}
