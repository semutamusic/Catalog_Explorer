package com.example.catalogExplorer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

public class MainActivity extends AppCompatActivity {
    static CatalogManager currMan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        setTheme(R.style.Base_Theme_CatalogExplorer);
        setContentView(R.layout.activity_main);

        //Instantiate CatalogManagers
        Map<String, CatalogManager> catalogManagerMap = new HashMap<>();
        String[] managerChoices = getResources().getStringArray(R.array.main_manager_choices);
        catalogManagerMap.put(managerChoices[0], LccManager.getInstance(this));
        catalogManagerMap.put(managerChoices[1], DdcManager.getInstance(this));
        catalogManagerMap.put(managerChoices[2], AuthorManager.getInstance(this));

        //Instantiate dropdown menu
        Spinner managerSelector = findViewById(R.id.main_header4_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.main_manager_choices,
                R.layout.spinner_item_text
        );
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item_text);
        managerSelector.setAdapter(adapter);
        managerSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                currMan = catalogManagerMap.get(adapterView.getItemAtPosition(i).toString());
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        //Instantiate Buttons
        instantiateActivityButton(R.id.main_guess_button, GameGuessActivity.class);
        instantiateActivityButton(R.id.main_searchCode_button, SearchCodeActivity.class);
        instantiateActivityButton(R.id.main_searchKeyword_button, SearchKeywordActivity.class);
        instantiateActivityButton(R.id.main_browse_button, BrowserActivity.class);
        instantiateActivityButton(R.id.main_help_button, HelpActivity.class);
    }

    private void instantiateActivityButton(int buttonId, Class activity){
        Button button = findViewById(buttonId);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchActivity(activity);
            }
        });
    }

    private void switchActivity(Class newActivity){
        Intent newIntent = new Intent(this, newActivity);
        startActivity(newIntent);
        overridePendingTransition(R.anim.flip_forward, R.anim.stay_still);
    }
}