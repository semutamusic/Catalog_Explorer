package com.example.libraryToolbox;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

class Line{
    TextView line;
    ImageView whiteout;
    boolean isLocked;

    public void lock(){
        whiteout.setVisibility(View.VISIBLE);
        isLocked = true;
    }
    public void unlock(){
        whiteout.setVisibility(View.INVISIBLE);
        isLocked = false;
    }
    public void setLineText(String text){
        if(!isLocked){
            line.setText(text);
        }
    }
    public String getLineText(){
        return line.getText().toString();
    }
}

public class RandomLOCGenerator extends AppCompatActivity {
    ImageButton generateButton, resetLocksButton, backButton;
    TextView classText, subclassText;
    LOCManager locMan;
    Line[] lines = new Line[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_locgenerator);
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        locMan = LOCManager.getInstance(this);

        generateButton = (ImageButton) findViewById(R.id.locGen_generate_button);
        resetLocksButton = (ImageButton) findViewById(R.id.locGen_resetLocks_button);
        backButton = (ImageButton) findViewById(R.id.locGen_back_button);

        //Set up Line array
        for(int i = 0; i < lines.length; i++){
            Line tempLine = new Line();
            tempLine.line = findViewById(getResources().getIdentifier("locGen_line"+(i+1)+"_text", "id", getPackageName()));
            tempLine.whiteout = findViewById(getResources().getIdentifier("locGen_lineWhiteout"+(i+1)+"_image", "id", getPackageName()));
            tempLine.unlock();
            tempLine.line.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if(b) {
                        tempLine.lock();
                        tempLine.line.setCursorVisible(true);
                    }
                    else{
                        setLocLabels(lines[0].getLineText());
                        //Hide the keyboard if focus is lost
                        imm.hideSoftInputFromWindow(tempLine.line.getWindowToken(), 0);
                        tempLine.line.setCursorVisible(false);
                    }
                }
            });
            lines[i] = tempLine;
        }

        classText = findViewById(R.id.locGen_class_text);
        subclassText = findViewById(R.id.locGen_subclass_text);

        setLocLabels(lines[0].getLineText());

        //Generate button listener, only generates text for unlocked lines
        generateButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){ generateNewLOCCode(); }
        });

        //Unlocks all lines
        resetLocksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(Line l : lines){
                    l.unlock();
                }
            }
        });

        //Returns to main menu
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                finish();
            }
        });

        generateNewLOCCode();
    }

    //Sets LOC Class and Subclass labels
    public void setLocLabels(String code){
        String lc = locMan.getLOCClass(code);
        String lsc = locMan.getLOCDescription(code);
        Log.i("Set LOC Class Labels", lc+", "+lsc);
        classText.setText(lc);
        subclassText.setText(lsc);
    }

    public void generateNewLOCCode(){
        LocCode newCode = lines[0].isLocked ? locMan.getRandomCode(lines[0].getLineText()) : locMan.getRandomCode();
        String code = newCode.getLocClass();
        setLocLabels(code);

        float subd_f = newCode.getSubdivision();
        String format = subd_f == (int)subd_f ? "%.0f" : "%.2f";

        lines[0].setLineText(newCode.getLocClass());
        lines[1].setLineText(String.format(format, newCode.getSubdivision()));
        lines[2].setLineText(newCode.getCutter1().toString());
        lines[3].setLineText(Integer.toString(newCode.getYear()));
    }
}
