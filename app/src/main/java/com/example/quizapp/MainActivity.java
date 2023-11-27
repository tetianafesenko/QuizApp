package com.example.quizapp;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity implements QuizFragment.OnAnswerSelectedListener {

    private int currentQuestionIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showQuizFragment();
    }

    private void showQuizFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, QuizFragment.newInstance(currentQuestionIndex));
        ft.commit();
    }

    @Override
    public void onAnswerSelected(boolean isCorrect) {
        showResultToast(isCorrect);
        currentQuestionIndex++;

        if (currentQuestionIndex < QuizData.questions.size()) {
            showQuizFragment();
        } else {
            // The quiz is finished
            showQuizCompletionMessage();
        }
    }

    private void showQuizCompletionMessage() {
        // Display a message indicating that the quiz is completed
        Toast.makeText(this, "Quiz Completed!", Toast.LENGTH_SHORT).show();
    }

    private void showResultToast(boolean isCorrect) {
        String message = isCorrect ? "Correct!" : "Incorrect!";
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void showNextQuestion() {
        // Replace the current fragment with a new instance of QuizFragment
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, QuizFragment.newInstance(currentQuestionIndex));
        ft.commit();
    }

    private void updateProgressBar() {
        ProgressBar progressBar = findViewById(R.id.progressbar);
        int totalQuestions = QuizData.questions.size();
        int progress = (int) (((float) (currentQuestionIndex + 1) / totalQuestions) * 100);
        progressBar.setProgress(progress);

        // Set up background color for the new question
        int[] fragmentColors = getResources().getIntArray(R.array.fragmentColors);
        int colorIndex = currentQuestionIndex % fragmentColors.length;
        int backgroundColor = fragmentColors[colorIndex];
        findViewById(R.id.fragment_container).setBackgroundColor(backgroundColor);
    }

}