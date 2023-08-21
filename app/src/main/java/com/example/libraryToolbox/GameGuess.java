package com.example.libraryToolbox;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

class ButtonOption{
    ImageButton button;
    TextView text;
    boolean isCorrect;
}

public class GameGuess extends AppCompatActivity {

    final int RESULTS_DURATION = 2000;
    ImageButton backButton, newGameButton;
    ImageView gameRightImage, gameWrongImage;
    TextView scoreText, gameHintText;
    ButtonOption[] buttonOptions = new ButtonOption[3];
    int correctAns = 0, totalAns = 0;
    Random rand;
    LOCManager locMan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_guess);

        rand = new Random();
        locMan = LOCManager.getInstance(this);

        backButton = (ImageButton) findViewById(R.id.guess_back_button);
        newGameButton = (ImageButton) findViewById(R.id.guess_newGame_button);

        gameRightImage = (ImageView) findViewById(R.id.guess_gameRight_image);
        gameRightImage.setVisibility(View.INVISIBLE);
        gameWrongImage = (ImageView) findViewById(R.id.guess_gameWrong_image);
        gameWrongImage.setVisibility(View.INVISIBLE);

        scoreText = (TextView) findViewById(R.id.guess_score_text);
        gameHintText = (TextView) findViewById(R.id.guess_gameHint_text);

        for(int i = 0; i < 3; i++){
            final int i_f = i;
            buttonOptions[i_f] = new ButtonOption();
            buttonOptions[i_f].button = (ImageButton) findViewById(getResources().getIdentifier("guess_gameOption"+(i+1)+"_button", "id", getPackageName()));
            buttonOptions[i_f].text = (TextView) findViewById(getResources().getIdentifier("guess_gameOption" + (i+1) + "_buttonText", "id", getPackageName()));
            buttonOptions[i_f].isCorrect = false;

            buttonOptions[i_f].button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    processAnswer(buttonOptions[i_f].button, buttonOptions[i_f].isCorrect);
                }
            });
        }

        newGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetScore();
                setNewChoices();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        setNewChoices();
    }

    void processAnswer(ImageButton button, boolean isCorrect){
        disableAllButtons();
        showStamp(gameRightImage);
        if (isCorrect) {
            setGuessScore(++correctAns, ++totalAns);
        } else {
            setGuessScore(correctAns, ++totalAns);
            moveStamp(R.id.guess_gameWrong_image, button.getId());
            showStamp(gameWrongImage);
        }
    }

    void setNewChoices(){
        String locClass = "E";
        while(locClass.equals("E") || locClass.equals("F") || locClass.equals("Z")){
            locClass = locMan.getRandomCodePrefix("", 1);
        }
        String[] locSubclasses = new String[3];
        int correctAnswer = rand.nextInt(3);
        for(int i = 0; i < locSubclasses.length; i++){
            String newSubclass;
            do{
                newSubclass = locMan.getRandomCodePrefix(locClass, 2);
            }
            while(arrayDoesContain(locSubclasses, newSubclass));
            locSubclasses[i] = newSubclass;
            buttonOptions[i].text.setText(locMan.getLOCSubclass(newSubclass));
            buttonOptions[i].isCorrect = false;
            if(i == correctAnswer){
                buttonOptions[i].isCorrect = true;
                moveStamp(R.id.guess_gameRight_image, buttonOptions[i].button.getId());
                gameHintText.setText(newSubclass);
            }
        }
    }

    void moveStamp(int stampId, int optionId){
        ConstraintLayout layout = findViewById(R.id.guess_game_layout);
        ConstraintSet set = new ConstraintSet();
        set.clone(layout);
        set.connect(stampId, ConstraintSet.BOTTOM, optionId, ConstraintSet.BOTTOM);
        set.applyTo(layout);
    }

    void showStamp(ImageView stamp){
        stamp.setVisibility(View.VISIBLE);
        stamp.postDelayed(new Runnable() {
            @Override
            public void run() {
                stamp.setVisibility(View.INVISIBLE);
                if(stamp.getId() == R.id.guess_gameRight_image){
                    setNewChoices();
                }
            }
        }, RESULTS_DURATION);
    }

    void disableAllButtons(){
        disableButton(backButton);
        disableButton(newGameButton);
        for(ButtonOption bo : buttonOptions){
            disableButton(bo.button);
        }
    }

    void disableButton(ImageButton button){
        button.setEnabled(false);
        button.postDelayed(new Runnable() {
            @Override
            public void run() {
                button.setEnabled(true);
            }
        }, RESULTS_DURATION);
    }

    void resetScore(){
        correctAns = 0;
        totalAns = 0;
        setGuessScore(correctAns, totalAns);
    }

    void setGuessScore(int correct, int total){
        String score_s = "";
        if(total == 0){
            score_s = getString(R.string.guess_score_default);
        }
        else {
            score_s = correct + " / " + total + " (" + (int)Math.floor((float)correct / (float)total * 100) + "%)";
        }
        scoreText.setText(score_s);
    }

    boolean arrayDoesContain(String[] arr, String s){
        for(int i = 0; i < arr.length; i++){
            if(arr[i] != null && arr[i].equals(s)) {
                return true;
            }
        }
        return false;
    }
}