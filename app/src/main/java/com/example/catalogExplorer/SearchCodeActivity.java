package com.example.catalogExplorer;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class SearchCodeActivity extends AppCompatActivity {
    LinearLayout resultLayout;
    SearchField[] searchFields;
    CatalogManager currMan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_code);

        currMan = MainActivity.currMan;

        CatalogField[] fields = currMan.getFields();
        searchFields = new SearchField[fields.length];

        //Set up fields
        LinearLayout fieldsLayout = findViewById(R.id.searchCode_field_layout);
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        for(int i = 0; i < searchFields.length; i++){
            SearchField tempField = new SearchField(this, fields[i].getLabel(), fields[i].getKeyboardType());
            tempField.field.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if(b) {
                        //Lock field if focus gained
                        tempField.lock();
                        tempField.field.setCursorVisible(true);
                    }
                    else{
                        //Hide the keyboard if focus is lost
                        imm.hideSoftInputFromWindow(tempField.field.getWindowToken(), 0);
                        updateResultText();
                        tempField.field.setCursorVisible(false);
                    }
                }
            });
            fieldsLayout.addView(tempField.getView(this));
            searchFields[i] = tempField;
        }

        //Initialize results layout
        resultLayout = findViewById(R.id.searchCode_result_layout);
        updateResultText();

        //Initialize header
        TextView headerText = findViewById(R.id.searchCode_header2_text);
        headerText.setText(currMan.getManagerName() + getString(R.string.searchCode_header2_ext));

        //Initialize back button
        Button backButton = findViewById(R.id.searchCode_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                finish();
                overridePendingTransition(0,R.anim.flip_backward);
            }
        });

        //Initialize clear button
        Button clearButton = findViewById(R.id.searchCode_clear_button);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(SearchField field : searchFields){
                    field.clear();
                }
                updateResultText();
            }
        });

        //Initialize randomize button
        //Only randomizes unlocked lines
        Button randomizeButton = findViewById(R.id.searchCode_randomize_button);
        randomizeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){ generateNewCatalogCode(); }
        });
    }

    public void updateResultText(){
        String[] fieldValues = new String[searchFields.length];
        for(int i = 0; i < searchFields.length; i++) {
            fieldValues[i] = searchFields[i].getLineText();
        }
        CatalogCode searchCode = currMan.getNewCode(fieldValues);
        ArrayList<String> descriptions = currMan.getDescriptionTree(searchCode);
        resultLayout.removeAllViews();
        if(descriptions.size() == 0){
            resultLayout.addView(getResultTextViewDefault());
        }
        else {
            for (int i = 0; i < descriptions.size(); i++) {
                resultLayout.addView(getResultTextView(descriptions.get(i), i+1));
            }
        }
    }

    public TextView getResultTextViewDefault(){
        TextView tempView = getResultTextView(getString(R.string.searchCode_result_def), 0);
        tempView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        return tempView;
    }

    public TextView getResultTextView(String text, int depth){
        TextView tempView = new TextView(this);
        tempView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        tempView.setTextAppearance(R.style.header_1);
        tempView.setPadding(50 * depth, 5, 50, 5);
        tempView.setText(text);
        return tempView;
    }

    public void generateNewCatalogCode(){
        CatalogCode newCode = currMan.getRandomCode();

        CatalogField[] codeFields = newCode.getFields();
        for(int i = 0; i < searchFields.length; i++){
            searchFields[i].setLineText(codeFields[i].toString());
        }
        updateResultText();
    }
}

//One search field is initialized for each field in the CatalogManager's CatalogCode
class SearchField {
    EditText field;
    ImageView whiteout;
    boolean isLocked;

    public SearchField(Context context, String fieldName, int fieldKeyboardType){
        field = new EditText(context);
        field.setId(View.generateViewId());
        field.setHint(fieldName);
        field.setRawInputType(fieldKeyboardType);
        field.setSingleLine(true);
        field.setGravity(Gravity.CENTER);
        field.setTextAppearance(R.style.header_1);

        whiteout = new ImageView(context);
        whiteout.setId(View.generateViewId());
        whiteout.setBackgroundResource(R.drawable.whiteout);

        unlock();
    }
    public void clear(){
        unlock();
        field.setText("");
    }
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
            field.setText(text);
        }
    }
    public String getLineText(){
        return field.getText().toString();
    }

    public ConstraintLayout getView(Context context){
        ConstraintLayout result = new ConstraintLayout(context);
        result.setId(View.generateViewId());
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                300,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        field.setLayoutParams(layoutParams);
        whiteout.setLayoutParams(layoutParams);
        result.setLayoutParams(layoutParams);

        result.addView(whiteout);
        result.addView(field);

        return result;
    }
}
