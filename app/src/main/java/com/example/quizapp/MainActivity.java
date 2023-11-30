package com.example.quizapp;

import android.content.Context;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements QuizFragment.OnAnswerSelectedListener {

    private int currentQuestionIndex = 0;
    private int correctAnswerCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showQuizFragment();
    }

    private void showQuizFragment() {
        // Use add instead of replace to add the fragment to the back stack
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.fragment_container, QuizFragment.newInstance(currentQuestionIndex));
        ft.addToBackStack(null); // Add the transaction to the back stack
        ft.commit();
    }

    @Override
    public void onAnswerSelected(boolean isCorrect) {
        // Removed the call to showResultToast(isCorrect)

        if (isCorrect) {
            correctAnswerCount++;
        }

        currentQuestionIndex++;

        if (currentQuestionIndex < QuizData.questions.size()) {
            // Move the showQuizFragment() call here
            showQuizFragment();
        } else {
            // The quiz is finished
            showQuizCompletionMessage();
            showResultAlertDialog(correctAnswerCount); // Show the result dialog
        }
    }


    private void showQuizCompletionMessage() {
        // Display a message indicating that the quiz is completed
        Toast.makeText(this, "Quiz Completed!", Toast.LENGTH_SHORT).show();
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

    private void resetQuiz() {
        currentQuestionIndex = 0;
        correctAnswerCount = 0;
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
