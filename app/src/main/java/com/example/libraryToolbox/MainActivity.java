package com.example.libraryToolbox;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    ImageView helpImage;
    ImageButton guessButton, locGenButton, ficGenButton, locIndButton,
            guessHelpButton, locGenHelpButton, ficGenHelpButton, locIndHelpButton;
    TextView helpTitleText, helpText;
    ConstraintLayout helpLayout;

    LOCManager locMan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locMan = LOCManager.getInstance(this);

        helpImage = (ImageView) findViewById(R.id.main_help_image);

        guessButton = instantiateActivityButton(R.id.main_guess_button, GameGuess.class);
        locGenButton = instantiateActivityButton(R.id.main_locGen_button, RandomLOCGenerator.class);
        ficGenButton = instantiateActivityButton(R.id.main_ficGen_button, RandomFictionGenerator.class);
        locIndButton = instantiateActivityButton(R.id.main_locInd_button, LOCIndex.class);

        guessHelpButton = instantiateHelpButton(R.id.main_guessHelp_button, R.string.main_guessButton, R.string.main_guessHelp);
        locGenHelpButton = instantiateHelpButton(R.id.main_locGenHelp_button, R.string.main_locGenButton, R.string.main_locGenHelp);
        ficGenHelpButton = instantiateHelpButton(R.id.main_ficGenHelp_button, R.string.main_ficGenButton, R.string.main_ficGenHelp);
        locIndHelpButton = instantiateHelpButton(R.id.main_locIndHelp_button, R.string.main_locIndButton, R.string.main_locIndHelp);

        helpTitleText = (TextView) findViewById(R.id.main_helpTitle_text);
        helpText = (TextView) findViewById(R.id.main_help_text);
        helpLayout = (ConstraintLayout) findViewById(R.id.main_help_layout);
        helpLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideHelp();
            }
        });
        hideHelp();
    }

    private ImageButton instantiateActivityButton(int buttonId, Class activity){
        ImageButton button = (ImageButton) findViewById(buttonId);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchActivity(activity);
            }
        });
        return button;
    }

    private void switchActivity(Class newActivity){
        Intent newIntent = new Intent(this, newActivity);
        startActivity(newIntent);
    }

    private ImageButton instantiateHelpButton(int buttonId, int helpTitleId, int helpId){
        ImageButton button = (ImageButton) findViewById(buttonId);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayHelp(helpTitleId, helpId);
            }
        });
        return button;
    }

    private void displayHelp(int helpTitleId, int helpId){
        String title = getString(helpTitleId);
        String text = getString(helpId);
        int imageHeight = (int)(text.length() * 3.5);

        ViewGroup.LayoutParams layoutParams = helpImage.getLayoutParams();
        layoutParams.height = imageHeight;
        helpImage.setLayoutParams(layoutParams);

        helpTitleText.setText(title);
        helpText.setText(text);
        helpLayout.setVisibility(View.VISIBLE);
    }

    private void hideHelp(){
        helpLayout.setVisibility(View.GONE);
    }
}