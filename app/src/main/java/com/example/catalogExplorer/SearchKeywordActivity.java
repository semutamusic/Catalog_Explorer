package com.example.catalogExplorer;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

public class SearchKeywordActivity extends AppCompatActivity {
    EditText searchText;
    LinearLayout resultsLayout;
    CatalogManager currMan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_keyword);

        currMan = MainActivity.currMan;

        resultsLayout = findViewById(R.id.searchKeyword_results_layout);

        //Initialize search field
        searchText = findViewById(R.id.searchKeyword_search_editText);
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        searchText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    searchText.setCursorVisible(true);
                }
                else{
                    imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0);
                    searchText.setCursorVisible(false);
                    updateResults();
                }
            }
        });

        //Initialize back button
        Button backButton = findViewById(R.id.searchKeyword_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(0,R.anim.flip_backward);
            }
        });

        //Initialize clear button
        Button clearButton = findViewById(R.id.searchKeyword_clear_button);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchText.setText("");
                resultsLayout.removeAllViews();
            }
        });

        //Initialize header
        TextView headerText = findViewById(R.id.searchKeyword_header2_text);
        headerText.setText(currMan.getManagerName() + getString(R.string.searchKeyword_header2_ext));
    }

    void updateResults(){
        String[] keywords = searchText.getText().toString().split(" ");
        resultsLayout.removeAllViews();

        boolean tooShort = false;
        for(String keyword : keywords){
            if(keyword.length() < 3){
                tooShort = true;
                break;
            }
        }

        if(keywords.length < 1){
            resultsLayout.removeAllViews();
        }
        else if(tooShort){
            resultsLayout.addView(getResultTextView(getString(R.string.searchKeyword_keywordTooShort_text)));
        }
        else {
            ArrayList<CatalogNode> resultNodes = currMan.getNodesByKeyword(keywords);

            if(resultNodes.size() == 0){
                resultsLayout.addView(getResultTextView(getString(R.string.searchKeyword_noResults_text)));
            }
            else {
                for (CatalogNode cn : resultNodes) {
                    ArrayList<String> descriptionTree = currMan.getDescriptionTree(cn.getCodeStart());
                    for(int i = 0; i < descriptionTree.size() - 1; i++){
                        resultsLayout.addView(getResultDescriptionTextView(descriptionTree.get(i), i));
                    }
                    resultsLayout.addView(getResultTextView(cn.toString()));
                }
            }
        }
    }

    TextView getResultTextView(String text){
        TextView tempText = new TextView(this);
        tempText.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        tempText.setText(text);
        tempText.setPadding(5, 5, 5, 25);
        tempText.setTextAppearance(R.style.result);
        return tempText;
    }

    TextView getResultDescriptionTextView(String text, int depth){
        TextView tempText = new TextView(this);
        tempText.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        tempText.setText(text);
        tempText.setPadding(5 + (25 * depth), 0, 5, 0);
        tempText.setTextAppearance(R.style.result_sub);
        return tempText;
    }
}