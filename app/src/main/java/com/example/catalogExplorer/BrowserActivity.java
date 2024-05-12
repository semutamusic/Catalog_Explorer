package com.example.catalogExplorer;

import android.content.Context;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class BrowserActivity extends AppCompatActivity {
    CatalogManager currMan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);

        currMan = MainActivity.currMan;

        //Set header
        TextView header = findViewById(R.id.browser_header_text);
        header.setText(currMan.getManagerName() + getString(R.string.browser_header_ext));

        //Initialize Scrollable with TieredLinearLayout
        ScrollView indexScrollable = findViewById(R.id.browser_index_scrollable);
        TieredLinearLayout topTierLayout = new TieredLinearLayout(this, currMan.getRoot(), 0);
        indexScrollable.addView(topTierLayout);

        //Initialize back button
        Button backButton = findViewById(R.id.browser_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(0,R.anim.flip_backward);
            }
        });
    }
}

class TieredLinearLayout extends LinearLayout {
    public TieredLinearLayout(Context context, CatalogNode catalogNode, int depth){
        super(context);

        //Initialize LayoutParams
        setId(View.generateViewId());
        setOrientation(VERTICAL);
        LinearLayout.LayoutParams layoutParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                );
        setLayoutParams(layoutParams);

        //Add buttons
        for(int i = 0; i < catalogNode.getSubnodes().size(); i++){
            CatalogNode node = (CatalogNode)catalogNode.getSubnodes().get(i);

            int buttonStyle = R.style.button;
            Button button = new Button(new ContextThemeWrapper(context, buttonStyle), null, buttonStyle);
            button.setId(View.generateViewId());
            button.setText(node.toString());
            button.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);

            ViewGroup.MarginLayoutParams buttonParams =
                    new MarginLayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                    );
            button.setPadding(10, 10, 10, 10);
            buttonParams.bottomMargin = 20;
            buttonParams.leftMargin = depth * 25;
            button.setLayoutParams(buttonParams);
            addView(button);

            button.setOnClickListener(new View.OnClickListener(){
                private boolean isOpen = false;
                private TieredLinearLayout tll;
                @Override
                public void onClick(View view){
                    isOpen = !isOpen;
                    if(isOpen){
                        tll = new TieredLinearLayout(context, node, depth + 1);
                        addView(tll, indexOfChild(button) + 1);
                    }
                    else{
                        removeView(tll);
                    }
                }
            });
        }
    }
}
