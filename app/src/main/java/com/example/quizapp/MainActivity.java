package com.example.quizapp;


import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements QuizFragment.OnAnswerSelectedListener {

    private int currentQuestionIndex = 0;
    private int correctAnswerCount = 0;
    private int totalAttempts = 0;
    private ArrayList<QuizResult> quizResults = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Load quiz results from file
        readQuizResultsFromFile();

        showQuizFragment();
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
            calculateAndShowAverage();
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

        totalAttempts++;

        currentQuestionIndex++; // Move this line after incrementing totalAttempts

        if (currentQuestionIndex < QuizData.questions.size()) {
            showQuizFragment();
        } else {
            saveQuizResult(correctAnswerCount);  // Save quiz result
            showQuizCompletionMessage();
            showResultAlertDialog(correctAnswerCount);
        }
    }

    private void showQuizCompletionMessage() {
        showToast("Quiz Completed!");
    }

    private void showResultToast(boolean isCorrect) {
        String message = isCorrect ? "Correct!" : "Incorrect!";
        showToast(message);
    }


    private void saveQuizResult(int score) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
        String date = dateFormat.format(new Date());

        QuizResult quizResult = new QuizResult(date, score);
        quizResults.add(quizResult);

        // Save quiz results to a file
        saveQuizResultsToFile();
    }

    private void saveQuizResultsToFile() {
        try (FileOutputStream fos = openFileOutput("quiz_results.txt", Context.MODE_PRIVATE);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(quizResults);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void readQuizResultsFromFile() {
        try (FileInputStream fis = openFileInput("quiz_results.txt");
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            quizResults = (ArrayList<QuizResult>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void calculateAndShowAverage() {
        if (totalAttempts > 0) {
            float average = (float) correctAnswerCount / totalAttempts * 100;
            String averageMessage = "Average: " + average + "%";
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
            // Reset saved results
            showToast("Results reset");
        });
        builder.setNegativeButton("No", (dialog, which) -> {
            // Cancellation
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
    private void resetQuiz() {
        currentQuestionIndex = 0;
    }
}
