package com.example.libraryToolbox;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ScrollView;

public class LOCIndex extends AppCompatActivity {

    ImageButton backButton;
    LOCManager locMan;
    ScrollView indexScrollable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loc_index);

        locMan = LOCManager.getInstance(this);

        backButton = (ImageButton) findViewById(R.id.locInd_back_button);
        indexScrollable = (ScrollView) findViewById(R.id.locInd_index_scrollable);

        TieredConstraintLayout topTierLayout = new TieredConstraintLayout(this, locMan.getRoot());
        indexScrollable.addView(topTierLayout);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}

class TieredConstraintLayout extends ConstraintLayout{
    public TieredConstraintLayout(Context context, LocClass lc){
        super(context);

        //Initialize LayoutParams
        setId(View.generateViewId());
        ViewGroup.LayoutParams layoutParams =
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                );
        setLayoutParams(layoutParams);

        //Initialize ConstraintSet
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(this);

        listOfButtons(context, constraintSet, lc);

        //Render ConstraintSet
        constraintSet.applyTo(this);
    }

    void listOfButtons(Context context, ConstraintSet constraintSet, LocClass lc){
        int lastId = -1;
        for(int i = 0; i < lc.getSubclasses().size(); i++) {
            Button newButton = new Button(context);
            newButton.setId(View.generateViewId());
            newButton.setText(lc.getSubclasses().get(i).toString());
            newButton.setTextAlignment(TEXT_ALIGNMENT_TEXT_START);
            addView(newButton);

            constraintSet.constrainWidth(newButton.getId(), ConstraintSet.MATCH_CONSTRAINT);
            constraintSet.constrainHeight(newButton.getId(), ConstraintSet.WRAP_CONTENT);

            constraintSet.connect(newButton.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
            constraintSet.connect(newButton.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
            if(lastId < 0) {
                constraintSet.connect(newButton.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 15);
            }
            else{
                constraintSet.connect(newButton.getId(), ConstraintSet.TOP, lastId, ConstraintSet.BOTTOM, 15);
            }

            lastId = newButton.getId();
        }
    }
}