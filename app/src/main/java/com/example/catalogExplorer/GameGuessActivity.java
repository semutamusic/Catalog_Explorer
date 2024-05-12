package com.example.catalogExplorer;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

public class GameGuessActivity extends AppCompatActivity {

    final int RESULTS_DURATION = 2000;
    Button backButton, newGameButton;
    Button option1Button, option2Button, option3Button;
    ImageView gameRightImage, gameWrongImage;
    TextView scoreText, gameHintText;
    int correctCount = 0, totalCount = 0;
    int correctChoice = 0;
    Random rand;
    CatalogManager currMan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_guess);

        rand = new Random();

        currMan = MainActivity.currMan;

        //Initialize right / wrong images
        gameRightImage = findViewById(R.id.guess_gameRight_image);
        gameRightImage.setVisibility(View.INVISIBLE);
        gameWrongImage = findViewById(R.id.guess_gameWrong_image);
        gameWrongImage.setVisibility(View.INVISIBLE);

        //Initialize text
        scoreText = findViewById(R.id.guess_score_text);
        gameHintText = findViewById(R.id.guess_gameHint_text);

        //Initialize choice buttons
        option1Button = findViewById(R.id.guess_gameOption1_button);
        option2Button = findViewById(R.id.guess_gameOption2_button);
        option3Button = findViewById(R.id.guess_gameOption3_button);
        instantiateChoiceButton(option1Button, 0);
        instantiateChoiceButton(option2Button, 1);
        instantiateChoiceButton(option3Button, 2);

        //Initialize new game button
        newGameButton = findViewById(R.id.guess_newGame_button);
        newGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetScore();
                setNewChoices();
            }
        });

        //Initialize back button
        backButton = findViewById(R.id.guess_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(0,R.anim.flip_backward);
            }
        });

        //Initialize header
        TextView header = findViewById(R.id.guess_header1_text);
        header.setText(currMan.getManagerName());

        setNewChoices();
    }

    void instantiateChoiceButton(Button button, int choice){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processAnswer(button, choice);
            }
        });
    }

    void processAnswer(Button button, int choice){
        disableAllButtons();
        showStamp(gameRightImage);
        if (choice == correctChoice) {
            setGuessScore(++correctCount, ++totalCount);
        } else {
            setGuessScore(correctCount, ++totalCount);
            moveStamp(gameWrongImage.getId(), button.getId());
            showStamp(gameWrongImage);
        }
    }

    void setNewChoices(){
        CatalogNode[] choiceNodes = currMan.getGuessGameChoices();
        correctChoice = rand.nextInt(3);

        option1Button.setText(choiceNodes[0].getGuessGameChoice());
        option2Button.setText(choiceNodes[1].getGuessGameChoice());
        option3Button.setText(choiceNodes[2].getGuessGameChoice());

        gameHintText.setText(choiceNodes[correctChoice].getGuessGameHint());

        int correctButtonId = -1;
        switch(correctChoice){
            case 0:
                correctButtonId = option1Button.getId();
                break;
            case 1:
                correctButtonId = option2Button.getId();
                break;
            case 2:
                correctButtonId = option3Button.getId();
                break;
            default:
                Log.w("Guessing Game Choices", "Correct Choice Out of Bounds");
                break;
        }
        moveStamp(gameRightImage.getId(), correctButtonId);
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
        disableButton(option1Button);
        disableButton(option2Button);
        disableButton(option3Button);
    }

    void disableButton(Button button){
        button.setEnabled(false);
        button.postDelayed(new Runnable() {
            @Override
            public void run() {
                button.setEnabled(true);
            }
        }, RESULTS_DURATION);
    }

    void resetScore(){
        correctCount = 0;
        totalCount = 0;
        setGuessScore(correctCount, totalCount);
    }

    void setGuessScore(int correct, int total){
        String score_s;
        if(total == 0){
            score_s = getString(R.string.guess_score_default);
        }
        else {
            score_s = correct + " / " + total + " (" + (int)Math.floor((float)correct / (float)total * 100) + "%)";
        }
        scoreText.setText(score_s);
    }
}