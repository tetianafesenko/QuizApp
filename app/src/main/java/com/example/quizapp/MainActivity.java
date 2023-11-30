package com.example.quizapp;


import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import java.io.FileOutputStream;
import androidx.appcompat.widget.Toolbar;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements QuizFragment.OnAnswerSelectedListener {

    private int currentQuestionIndex = 0;
    private int correctAnswerCount = 0;
    private int totalAttempts = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Find the custom_title TextView
        TextView customTitleTextView = findViewById(R.id.custom_title);

        customTitleTextView.setText(R.string.custom_quiz_assignment_title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.menu_get_average) {
            showAverageReport();
            return true;
        } else if (itemId == R.id.menu_select_questions) {
            showSelectQuestionsDialog();
            return true;
        } else if (itemId == R.id.menu_reset_results) {
            showResetResultsDialog();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }


    private void showQuizFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, QuizFragment.newInstance(currentQuestionIndex));
        ft.commit();
    }

    @Override
    public void onAnswerSelected(boolean isCorrect) {
        showResultToast(isCorrect);

        if (isCorrect) {
            correctAnswerCount++;
        }

        currentQuestionIndex++;
        totalAttempts++;

        if (currentQuestionIndex < QuizData.questions.size()) {
            showQuizFragment();
        } else {
            showQuizCompletionMessage();
            showResultAlertDialog(correctAnswerCount);
        }
    }

    private void showQuizCompletionMessage() {
        Toast.makeText(this, "Quiz Completed!", Toast.LENGTH_SHORT).show();
    }

    private void showResultToast(boolean isCorrect) {
        String message = isCorrect ? "Correct!" : "Incorrect!";
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showAverageReport() {
        if (totalAttempts > 0) {
            float average = (float) correctAnswerCount / totalAttempts;
            String averageMessage = "Average: " + average;
            showToast(averageMessage);
        } else {
            showToast("No attempts yet");
        }
    }

    private void showSelectQuestionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Number of Questions");
        builder.setMessage("There are 3 questions");
        builder.setPositiveButton("OK", (dialog, which) -> {
            // Selection
            showToast("3 questions selected");

            showQuizFragment();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            // Cancellation
            showToast("Dialog canceled");
        });
        builder.show();
    }


    private void showResetResultsDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reset Saved Results");
        builder.setMessage("Are you sure you want to reset saved results?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            // Reset saved results logic
            showToast("Results reset");
        });
        builder.setNegativeButton("No", (dialog, which) -> {
            // Handle cancellation
            showToast("Reset canceled");
        });
        builder.show();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showResultAlertDialog(int correctCount) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Quiz Finished");
        builder.setMessage("You answered " + correctCount + " questions correctly. Do you want to save the result?");

        builder.setPositiveButton("Save", (dialog, which) -> {
            saveResult(correctCount);
            resetQuiz();
        });

        builder.setNegativeButton("Ignore", (dialog, which) -> resetQuiz());

        builder.show();
    }

    private void resetQuiz() {
        currentQuestionIndex = 0;
        correctAnswerCount = 0;
        showQuizFragment();
    }

    private void saveResult(int correctCount) {
        String fileName = "quiz_result.txt";
        try (FileOutputStream fos = openFileOutput(fileName, Context.MODE_PRIVATE)) {
            String resultString = String.valueOf(correctCount);
            fos.write(resultString.getBytes());
            Toast.makeText(this, "Result saved!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving result", Toast.LENGTH_SHORT).show();
        }
    }
}
