package com.example.quizapp;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AlertDialog;
import java.io.FileOutputStream;
import android.content.Context;
import java.io.IOException;




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

    private void showResultAlertDialog(int correctCount) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Quiz Finished");
        builder.setMessage("You answered " + correctCount + " questions correctly. Do you want to save the result?");

        builder.setPositiveButton("Save", (dialog, which) -> {
            // Save the result (number of correct questions) to the file system
            saveResult(correctCount);

            // Reset the quiz for another attempt
            resetQuiz();
        });

        builder.setNegativeButton("Ignore", (dialog, which) -> {
            // Reset the quiz for another attempt without saving
            resetQuiz();
        });

        builder.show();
    }
    // Define the resetQuiz method
    private void resetQuiz() {
        currentQuestionIndex = 0;
        // You may need additional logic to reset other quiz-related variables or UI elements
        showQuizFragment(); // To show the first question again
    }
    private void saveResult(int correctCount) {
        // Create a file to save the result
        String fileName = "quiz_result.txt";
        try (FileOutputStream fos = openFileOutput(fileName, Context.MODE_PRIVATE)) {
            // Write the correct count to the file
            String resultString = String.valueOf(correctCount);
            fos.write(resultString.getBytes());
            Toast.makeText(this, "Result saved!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving result", Toast.LENGTH_SHORT).show();
        }
    }
}