package com.example.flagquizapp;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    private ImageView flagImageView;
    private RadioGroup answerGroup;
    private RadioButton option1, option2, option3, option4;
    private Button submitBtn, hintBtn;
    private ArrayList<QuizQuestion> questions;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private int hintUsed = 0;
    private int numberOfQuestions = 5;
    private String difficulty = "Easy";
    private TextView scoreTextView;

    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "FlagQuizPrefs";
    private static final String KEY_SCORE = "score";
    private static final String KEY_HIGHEST_SCORE = "highest_score";
    private static final String KEY_DIFFICULTY = "difficulty";
    private static final String KEY_NUMBER_OF_QUESTIONS = "number_of_questions";


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Retrieve data from Intent
        Intent intent = getIntent();
        difficulty = intent.getStringExtra("difficulty");
        numberOfQuestions = intent.getIntExtra("numberOfQuestions", 5);

        // Initialize Views
        flagImageView = findViewById(R.id.flagImageView);
        answerGroup = findViewById(R.id.answerGroup);
        option1 = findViewById(R.id.option1);
        option2 = findViewById(R.id.option2);
        option3 = findViewById(R.id.option3);
        option4 = findViewById(R.id.option4);
        submitBtn = findViewById(R.id.submitBtn);
        hintBtn = findViewById(R.id.hintBtn);
        scoreTextView = findViewById(R.id.scoreTextView);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        //score = sharedPreferences.getInt(KEY_SCORE, 0);
        difficulty = sharedPreferences.getString(KEY_DIFFICULTY, "Medium");

        scoreTextView.setText("Score: " + score);

        // Initialize Questions
        questions = getQuestions();
        Collections.shuffle(questions);  // Shuffle the questions

        // Limit the number of questions based on user input
        if (questions.size() > numberOfQuestions) {
            questions = new ArrayList<>(questions.subList(0, numberOfQuestions));
        }

        // Display the first question
        showNextQuestion();

        // Submit Button Logic
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer();
            }
        });

        // Hint Button Logic
        hintBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hintUsed == 0) {
                    useHint();
                    hintUsed = 1;
                } else {
                    Toast.makeText(MainActivity.this, "Hint already used!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showNextQuestion() {
        if (currentQuestionIndex < questions.size()) {
            // Clear previous selection
            answerGroup.clearCheck();
            hintUsed = 0;

            // Enable all options
            option1.setEnabled(true);
            option2.setEnabled(true);
            option3.setEnabled(true);
            option4.setEnabled(true);

            QuizQuestion currentQuestion = questions.get(currentQuestionIndex);

            // Set flag image
            flagImageView.setImageResource(currentQuestion.getFlagImageResId());

            // Set multiple-choice answers
            ArrayList<String> answers = currentQuestion.getAnswers();
            Collections.shuffle(answers);  // Randomize answers
            option1.setText(answers.get(0));
            option2.setText(answers.get(1));
            option3.setText(answers.get(2));
            option4.setText(answers.get(3));
        } else {
            // Quiz finished
            saveScoreAndLevel();  // Save final score and level
        }
    }

    @SuppressLint("SetTextI18n")
    private void checkAnswer() {
        int selectedOptionId = answerGroup.getCheckedRadioButtonId();
        RadioButton selectedOption = findViewById(selectedOptionId);

        if (selectedOption != null) {
            QuizQuestion currentQuestion = questions.get(currentQuestionIndex);
            if (selectedOption.getText().equals(currentQuestion.getCorrectAnswer())) {
                score++;
                scoreTextView.setText("Score: " + score);  // Update score TextView
            }
            currentQuestionIndex++;
            hintUsed = 0;
            showNextQuestion();
        } else {
            Toast.makeText(MainActivity.this, "Please select an option!", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveScoreAndLevel() {
        // Save the current score
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_SCORE, score);

        // Check and save the highest score
        int highestScore = sharedPreferences.getInt("highest_score", 0);
        if (score > highestScore) {
            editor.putInt("highest_score", score);
        }

        // Toast.makeText(MainActivity.this, "Quiz Finished! Your Score: " + score + ", Difficulty: " + difficulty, Toast.LENGTH_LONG).show();
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Quiz Finished!")
                .setMessage("Your Score: " + score + "\nDifficulty: " + difficulty + "\nHighest Score: " + highestScore)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Action to perform when the OK button is clicked (dismisses the dialog by default)
                        dialog.dismiss();
                        Intent intent = new Intent(MainActivity.this, LaunchActivity.class);
                        startActivity(intent);
                    }
                })
                .setCancelable(false) // To prevent closing the dialog by tapping outside it
                .show();

        editor.apply();
    }

    private void useHint() {
        QuizQuestion currentQuestion = questions.get(currentQuestionIndex);
        ArrayList<String> wrongAnswers = currentQuestion.getWrongAnswers();

        // Disable two incorrect options
        if (option1.getText().equals(wrongAnswers.get(0)) || option1.getText().equals(wrongAnswers.get(1))) {
            option1.setEnabled(false);
        } else if (option2.getText().equals(wrongAnswers.get(0)) || option2.getText().equals(wrongAnswers.get(1))) {
            option2.setEnabled(false);
        } else if (option3.getText().equals(wrongAnswers.get(0)) || option3.getText().equals(wrongAnswers.get(1))) {
            option3.setEnabled(false);
        } else {
            option4.setEnabled(false);
        }
    }

    private ArrayList<QuizQuestion> getQuestions() {
        ArrayList<QuizQuestion> questions = new ArrayList<>();
        questions.add(new QuizQuestion(R.drawable.flag_usa, "United States", "Canada", "Mexico", "Brazil"));
        questions.add(new QuizQuestion(R.drawable.flag_france, "France", "Italy", "Spain", "Germany"));
        questions.add(new QuizQuestion(R.drawable.flag_japan, "Japan", "South Korea", "China", "Thailand"));
        questions.add(new QuizQuestion(R.drawable.flag_germany, "Germany", "Austria", "Belgium", "Switzerland"));
        questions.add(new QuizQuestion(R.drawable.flag_canada, "Canada", "United States", "Mexico", "Australia"));
        questions.add(new QuizQuestion(R.drawable.flag_brazil, "Brazil", "Argentina", "Chile", "Peru"));
        questions.add(new QuizQuestion(R.drawable.flag_australia, "Australia", "New Zealand", "South Africa", "United Kingdom"));
        questions.add(new QuizQuestion(R.drawable.flag_india, "India", "Pakistan", "Sri Lanka", "Bangladesh"));
        questions.add(new QuizQuestion(R.drawable.flag_russia, "Russia", "Ukraine", "Belarus", "Poland"));
        questions.add(new QuizQuestion(R.drawable.flag_south_africa, "South Africa", "Kenya", "Nigeria", "Ghana"));
        questions.add(new QuizQuestion(R.drawable.flag_spain, "Spain", "Portugal", "Italy", "France"));
        questions.add(new QuizQuestion(R.drawable.flag_italy, "Italy", "Spain", "Greece", "France"));
        questions.add(new QuizQuestion(R.drawable.flag_argentina, "Argentina", "Brazil", "Chile", "Uruguay"));
        questions.add(new QuizQuestion(R.drawable.flag_china, "China", "Japan", "South Korea", "Vietnam"));
        questions.add(new QuizQuestion(R.drawable.flag_egypt, "Egypt", "Morocco", "Algeria", "Sudan"));
        return questions;
    }
}
