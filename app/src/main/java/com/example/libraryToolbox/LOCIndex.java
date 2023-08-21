package com.example.libraryToolbox;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LOCIndex extends AppCompatActivity {

    ImageButton backButton;
    LOCManager locMan;
    ScrollView indexScrollable;
    SeekBar indexSeekbarSeekbar;
    TextView classLetterText, classNameText, indexScrollableText;
    Map<String, indexListing> indexTexts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loc_index);

        locMan = LOCManager.getInstance(this);

        backButton = (ImageButton) findViewById(R.id.locInd_back_button);
        indexSeekbarSeekbar = (SeekBar) findViewById(R.id.locInd_indexSeekbar_seekbar);
        classLetterText = (TextView) findViewById(R.id.locInd_classLetter_text);
        classNameText = (TextView) findViewById(R.id.locInd_className_text);
        indexScrollableText = (TextView) findViewById(R.id.locInd_index_scrollableText);
        indexScrollable = (ScrollView) findViewById(R.id.locInd_index_scrollable);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        indexSeekbarSeekbar.setMax(locMan.getLOCString().length()-1);

        indexSeekbarSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                displayIndexListings(i);
                indexScrollable.scrollTo(0, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        indexTexts = new HashMap<>();

        for(int i = 0; i < indexSeekbarSeekbar.getMax()+1; i++){
            String classCode = Character.toString(locMan.getLOCString().charAt(i));
            List<String> codes = locMan.getAllCodesWithPrefix(classCode);
            indexListing il = new indexListing(locMan.getLOCClass(codes.get(0)));
            for(String c : codes){
                il.listings += '\n' + c + " - " + locMan.getLOCSubclass(c) + '\n';
            }
            indexTexts.put(classCode, il);
        }

        displayIndexListings(indexSeekbarSeekbar.getProgress());
    }

    public void displayIndexListings(int ind){
        String classCode = Character.toString(locMan.getLOCString().charAt(ind));
        indexListing il = indexTexts.get(classCode);

        classLetterText.setText(classCode);
        classNameText.setText(il.name);
        indexScrollableText.setText(il.listings);
    }
}

class indexListing {
    public String name;
    public String listings = "";

    public indexListing(String n){
        name = n;
    }
}