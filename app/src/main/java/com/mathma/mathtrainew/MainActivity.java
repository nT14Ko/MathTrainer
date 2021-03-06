package com.mathma.mathtrainew;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private TextView textViewQuestion;
    private TextView textViewTimer;
    private TextView textViewScore;
    private ArrayList<TextView> options = new ArrayList<>();

    private int rightAnswer;
    private int rightAnswerPosition;
    private int min = 3;
    private int max = 10;
    private int countOfQuestions = 0;
    private int countOfRightAnswers = 0;
    private boolean gameOver = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textViewTimer = findViewById(R.id.textViewTimer);
        TextView opinion0 = findViewById(R.id.textViewOpinion0);
        TextView opinion1 = findViewById(R.id.textViewOpinion1);
        TextView opinion2 = findViewById(R.id.textViewOpinion2);
        TextView opinion3 = findViewById(R.id.textViewOpinion3);
        textViewQuestion = findViewById(R.id.textViewQuestion);
        textViewScore = findViewById(R.id.textViewScore);
        options.add(opinion0);
        options.add(opinion1);
        options.add(opinion2);
        options.add(opinion3);
        playNext();
        initGame();

    }
    private void timer(){
        CountDownTimer timer = new CountDownTimer(50000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                textViewTimer.setText(getTime(millisUntilFinished));
                if (millisUntilFinished < 10000) {
                    textViewTimer.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                }
            }

            @Override
            public void onFinish() {
                gameOver = true;
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                int max = preferences.getInt("max", 0);
                if (countOfRightAnswers >= max) {
                    preferences.edit().putInt("max", countOfRightAnswers).apply();
                }
                Intent intent = new Intent(MainActivity.this, ScoreActivity.class);
                intent.putExtra("result", countOfRightAnswers);
                startActivity(intent);
            }
        };
        timer.start();
    }

    private void playNext() {
        generateQuestion();
        for (int i = 0; i < options.size(); i++) {
            if (i == rightAnswerPosition) {
                options.get(i).setText(Integer.toString(rightAnswer));
            } else {
                options.get(i).setText(Integer.toString(generateWrongAnswer()));
            }
        }
        String score = String.format("%s / %s", countOfRightAnswers, countOfQuestions);
        textViewScore.setText(score);
    }

    private void generateQuestion() {
        int a = (int) (Math.random() * (max - min + 1) + min);
        int b = (int) (Math.random() * (max - min + 1) + min);
        int mark = (int) (Math.random() * 2);
        boolean isPositive = mark == 1;
        String question;
        if (isPositive) {
            rightAnswer = a + b;
            question = String.format("%s + %s", a, b);
        } else {
            rightAnswer = a - b;
            question = String.format("%s - %s", a, b);
        }
        textViewQuestion.setText(question);
        rightAnswerPosition = (int) (Math.random() * 4);
    }

    private int generateWrongAnswer() {
        int result;
        do {
            result = (int) (Math.random() * max * 2 + 1) - (max - min);
        } while (result == rightAnswer);
        return result;
    }

    private String getTime(long millis) {
        int seconds = (int) (millis / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    public void onClickAnswer(View view) {
        if (!gameOver) {
            TextView textView = (TextView) view;
            String answer = textView.getText().toString();
            int chosenAnswer = Integer.parseInt(answer);
            if (chosenAnswer == rightAnswer) {
                countOfRightAnswers++;
                Toast.makeText(this, R.string.yes, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.no, Toast.LENGTH_SHORT).show();
            }
            if (countOfRightAnswers == 5){
                Intent intent = new Intent(MainActivity.this, Game2.class);
                startActivity(intent);
            }
            countOfQuestions++;
            playNext();
        }
    }
    private void initGame(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("This is a training level. To go to the game and save the result, dial 5 points");
        builder.setPositiveButton("Deal!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                timer();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
