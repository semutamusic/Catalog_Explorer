package com.example.libraryToolbox;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

public class RandomFictionGenerator extends AppCompatActivity {

    private final int INK_SPLOTCH_CHANCE = 3;
    ImageButton backButton, generateButton;
    TextView authorText;
    ImageView authorInkSplotchImage;
    FictionManager ficMan;
    Random rand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_fiction_generator);

        ficMan = FictionManager.getInstance(this);

        backButton = (ImageButton) findViewById(R.id.ficGen_back_button);
        generateButton = (ImageButton) findViewById(R.id.ficGen_generate_button);
        authorText = (TextView) findViewById(R.id.ficGen_author_text);
        authorInkSplotchImage = (ImageView) findViewById(R.id.ficGen_authorInkSplotch_image);

        rand = new Random();

        generateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { generateNewAuthor(); }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { finish(); }
        });

        generateNewAuthor();
    }

    public void generateNewAuthor(){
        String author = ficMan.getRandomAuthor();
        authorText.setText(author);

        //Randomly spill ink on the author's name
        if(rand.nextInt(INK_SPLOTCH_CHANCE) == 0 && author.length() > 3){
            authorInkSplotchImage.setVisibility(View.VISIBLE);
        }
        else{
            authorInkSplotchImage.setVisibility(View.INVISIBLE);
        }
    }
}