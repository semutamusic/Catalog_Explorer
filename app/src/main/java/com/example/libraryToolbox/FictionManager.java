package com.example.libraryToolbox;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FictionManager {

    private static FictionManager instance;
    List<String> authors;
    Random rand;

    public static FictionManager getInstance(Context context){
        if(instance == null){
            instance = new FictionManager(context);
        }
        return instance;
    }

    private FictionManager(Context context){
        authors = new ArrayList<>();
        rand = new Random();
        try {
            InputStream is = context.getResources().openRawResource(R.raw.fiction_author_last_names);
            BufferedReader bf = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = bf.readLine()) != null){
                authors.add(line);
            }
            Log.i("FictionManager", authors.size()+" names added");
        }
        catch(Exception e){
            Log.e("FictionManager Error", e.toString());
        }
    }

    public String getRandomAuthor(){
        return authors.get(rand.nextInt(authors.size()));
    }
}
