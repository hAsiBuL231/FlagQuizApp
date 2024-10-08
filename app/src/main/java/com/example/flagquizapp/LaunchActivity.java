package com.example.flagquizapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LaunchActivity extends AppCompatActivity {

    private RadioGroup difficultyGroup, questionNumberGroup;
    private Button startQuizButton;

    private TextView highestScoreTextView;

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
        setContentView(R.layout.activity_launch);

        // Initialize Views
        difficultyGroup = findViewById(R.id.difficultyGroup);
        questionNumberGroup = findViewById(R.id.questionNumberGroup);
        startQuizButton = findViewById(R.id.startQuizButton);
        highestScoreTextView = findViewById(R.id.highestScoreTextView);

        // Get the highest score from SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int highestScore = sharedPreferences.getInt(KEY_HIGHEST_SCORE, 0);

        // Display the highest score
        highestScoreTextView.setText("Highest Score: " + highestScore);

        // Load saved difficulty and number of questions, or set default values
        loadSavedPreferences();

        // Start Quiz Button Logic
        startQuizButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startQuiz();
            }
        });
    }

    private void loadSavedPreferences() {
        // Retrieve saved difficulty and number of questions from SharedPreferences
        String savedDifficulty = sharedPreferences.getString(KEY_DIFFICULTY, "Medium");
        int savedNumberOfQuestions = sharedPreferences.getInt(KEY_NUMBER_OF_QUESTIONS, 5);

        // Set saved difficulty radio button selection
        for (int i = 0; i < difficultyGroup.getChildCount(); i++) {
            RadioButton radioButton = (RadioButton) difficultyGroup.getChildAt(i);
            if (radioButton.getText().toString().equals(savedDifficulty)) {
                radioButton.setChecked(true);
                break;
            }
        }

        // Set saved number of questions radio button selection
        for (int i = 0; i < questionNumberGroup.getChildCount(); i++) {
            RadioButton radioButton = (RadioButton) questionNumberGroup.getChildAt(i);
            if (Integer.parseInt(radioButton.getText().toString()) == savedNumberOfQuestions) {
                radioButton.setChecked(true);
                break;
            }
        }
    }

    private void startQuiz() {
        // Get selected difficulty level
        int selectedDifficultyId = difficultyGroup.getCheckedRadioButtonId();
        RadioButton selectedDifficulty = findViewById(selectedDifficultyId);

        // Get selected number of questions
        int selectedQuestionNumberId = questionNumberGroup.getCheckedRadioButtonId();
        RadioButton selectedQuestionNumber = findViewById(selectedQuestionNumberId);

        if (selectedDifficulty == null || selectedQuestionNumber == null) {
            Toast.makeText(LaunchActivity.this, "Please select both difficulty level and number of questions!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the values as String
        String difficulty = selectedDifficulty.getText().toString();
        int numberOfQuestions = Integer.parseInt(selectedQuestionNumber.getText().toString());

        // Save the selected values in SharedPreferences
        savePreferences(difficulty, numberOfQuestions);

        // Start MainActivity and pass the selected options
        Intent intent = new Intent(LaunchActivity.this, MainActivity.class);
        intent.putExtra("difficulty", difficulty);
        intent.putExtra("numberOfQuestions", numberOfQuestions);
        startActivity(intent);
    }

    private void savePreferences(String difficulty, int numberOfQuestions) {
        // Save the difficulty and number of questions in SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_DIFFICULTY, difficulty);
        editor.putInt(KEY_NUMBER_OF_QUESTIONS, numberOfQuestions);
        editor.apply();
    }
}



