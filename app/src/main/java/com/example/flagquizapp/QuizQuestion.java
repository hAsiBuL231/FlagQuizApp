package com.example.flagquizapp;
import java.util.ArrayList;
import java.util.Arrays;

public class QuizQuestion {
    private int flagImageResId;
    private String correctAnswer;
    private ArrayList<String> answers;

    public QuizQuestion(int flagImageResId, String correctAnswer, String... wrongAnswers) {
        this.flagImageResId = flagImageResId;
        this.correctAnswer = correctAnswer;
        this.answers = new ArrayList<>();
        this.answers.add(correctAnswer);
        this.answers.addAll(Arrays.asList(wrongAnswers));
    }

    public int getFlagImageResId() {
        return flagImageResId;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public ArrayList<String> getAnswers() {
        return answers;
    }

    public ArrayList<String> getWrongAnswers() {
        ArrayList<String> wrongAnswers = new ArrayList<>(answers);
        wrongAnswers.remove(correctAnswer);  // Remove the correct answer
        return wrongAnswers;
    }
}
