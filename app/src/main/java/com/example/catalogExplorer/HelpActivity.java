package com.example.catalogExplorer;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class HelpActivity extends AppCompatActivity {
    CatalogManager currMan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        currMan = MainActivity.currMan;

        //Set header
        TextView headerText = findViewById(R.id.help_header1_text);
        headerText.setText(currMan.getManagerName());

        //Set text
        TextView helpText = findViewById(R.id.help_text_text);
        helpText.setText(currMan.getHelpText());

        //Initialize back button
        Button backButton = findViewById(R.id.help_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.stay_still, R.anim.flip_backward);
            }
        });
    }
}