package com.example.quizapp;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class QuizFragment extends Fragment {

    private int currentQuestionIndex;
    private OnAnswerSelectedListener answerSelectedListener;

    public interface OnAnswerSelectedListener {
        void onAnswerSelected(boolean isCorrect);
    }

    public static QuizFragment newInstance(int questionIndex) {
        QuizFragment fragment = new QuizFragment();
        Bundle args = new Bundle();
        args.putInt("questionIndex", questionIndex);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            answerSelectedListener = (OnAnswerSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context + " must implement OnAnswerSelectedListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz, container, false);

        assert getArguments() != null;
        currentQuestionIndex = getArguments().getInt("questionIndex");

        // Set up your UI elements and question based on the currentQuestionIndex
        TextView questionTextView = view.findViewById(R.id.questionTextView);
        questionTextView.setText(QuizData.questions.get(currentQuestionIndex).getText());

        Button btnTrue = view.findViewById(R.id.trueButton);
        Button btnFalse = view.findViewById(R.id.falseButton);

        btnTrue.setOnClickListener(v -> checkAnswer(true));
        btnFalse.setOnClickListener(v -> checkAnswer(false));

        ProgressBar progressBar = view.findViewById(R.id.progressbar);
        int totalQuestions = QuizData.questions.size();
        int progress = (int) (((float) (currentQuestionIndex + 1) / totalQuestions) * 100);
        progressBar.setProgress(progress);

        return view;

    }

    private void checkAnswer(boolean userAnswer) {
        // Get the current question from QuizData
        QuizData.Question currentQuestion = QuizData.questions.get(currentQuestionIndex);

        // Compare userAnswer with the correct answer for the current question
        boolean isCorrect = (userAnswer == currentQuestion.getAnswer());

        // Notify the listener with the result
        answerSelectedListener.onAnswerSelected(isCorrect);
    }
}
