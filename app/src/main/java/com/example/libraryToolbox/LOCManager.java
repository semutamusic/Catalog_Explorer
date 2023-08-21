package com.example.libraryToolbox;

import android.content.Context;
import android.util.Log;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class LOCManager {

    private final String INIT_STRING = "_init";
    private static LOCManager instance;
    Context context;
    Map<String, LOCCode> locCodes;
    Map<String, String> locStrings;
    Random rand;

    public static LOCManager getInstance(Context context){
        if(instance == null){
            instance = new LOCManager(context);
        }
        return instance;
    }

    private LOCManager(Context c){
        //Contains LOCCode classes, holds metadata for each individual LOC class
        locCodes = new HashMap<String, LOCCode>();
        //Contains strings that map out valid LOC classes. Replace with tree structure?
        locStrings = new HashMap<String, String>();
        context = c;
        rand = new Random();
        try{
            //Get CSV file from res/raw
            InputStream is = context.getResources().openRawResource(R.raw.loc_code_ranges);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            CSVReader reader = new CSVReaderBuilder(br).withSkipLines(1).build();
            //Read in
            String[] nextLine;
            while((nextLine = reader.readNext()) != null){
                //Read codes into map
                locCodes.put(nextLine[0],
                        new LOCCode(nextLine[0],
                                nextLine[1],
                                nextLine[2],
                                Integer.parseInt(nextLine[3]),
                                Integer.parseInt(nextLine[4])
                        )
                );
                //Generate strings into map
                for(int i = 0; i < nextLine[0].length(); i++){
                    String codeSubstring = nextLine[0].substring(0, i+1);

                    String codeKey = codeSubstring.length() < 2 ? INIT_STRING : codeSubstring.substring(0, codeSubstring.length()-1);
                    String nextCharacter = Character.toString(codeSubstring.charAt(codeSubstring.length()-1));

                    if(locStrings.containsKey(codeKey)){
                        if(!locStrings.get(codeKey).contains(nextCharacter)){
                            locStrings.put(codeKey, locStrings.get(codeKey)+nextCharacter);
                        }
                    }
                    else{
                        locStrings.put(codeKey, nextCharacter);
                    }
                }
                if(locStrings.containsKey(nextLine[0])){
                    locStrings.put(nextLine[0], locStrings.get(nextLine[0])+" ");
                }
                else{
                    locStrings.put(nextLine[0], " ");
                }
            }
            Log.i("CSV Read In", locCodes.size()+" lines read");
            Log.i("LOC Codes", locCodes.entrySet().toString());
            Log.i("LOC Strings", locStrings.entrySet().toString());
        }
        catch(Exception e){ Log.e("CSV Read Error", e.toString()); }
    }

    public String getRandomCode(){
        return getRandomCodePrefix("", 3);
    }

    public String getRandomCodePrefix(String code, int ttl){
        Log.i("Random Code Prefix", "Code: "+code+", TTL: "+ttl);
        ttl--;
        if(code.equals("")){
            code += getRandomCharacter(locStrings.get(INIT_STRING));
            return getRandomCodePrefix(code, ttl);
        }
        if(locStrings.get(code) == null || ttl < 0){
            return code;
        }
        String nextChar = getRandomCharacter(locStrings.get(code));
        return nextChar.equals(" ") ? code : getRandomCodePrefix(code + nextChar, ttl);
    }

    public List<String> getAllCodesWithPrefix(String code){
        List<String> codes = new ArrayList<>();

        String prefixString = locStrings.get(code);
        if(prefixString == null){
            return codes;
        }

        if(prefixString.equals(" ") || prefixString.charAt(0) == ' '){
            codes.add(code);
        }

        for(int i = 0; i < prefixString.length(); i++){
            codes.addAll(getAllCodesWithPrefix(code + prefixString.charAt(i)));
        }

        return codes;
    }

    public String getRandomRange(String locClass){
        LOCCode code = locCodes.get(locClass);
        if(code == null){
            return null;
        }
        String range = Integer.toString(code.start + rand.nextInt(code.end - code.start));
        return range;
    }

    public String getRandomAuthor(){
        String author = ".";
        author += Character.toString((char)(rand.nextInt(90-65)+65));
        author += Integer.toString(rand.nextInt(999));
        return author;

    }

    public String getRandomYear(){
        String year = Integer.toString(1900 + rand.nextInt(2023 - 1900));
        return year;
    }

    public String getLOCClass(String code) {
        LOCCode lc = locCodes.get(code);
        if(lc != null){
            return lc.locClass;
        }
        return context.getString(R.string.default_class);
    }

    public String getLOCSubclass(String code) {
        LOCCode lsc = locCodes.get(code);
        if(lsc != null){
            return lsc.locSubclass;
        }
        return context.getString(R.string.default_subclass);
    }

    public String getLOCString(){
        return getLOCString(INIT_STRING);
    }

    public String getLOCString(String s){
        return locStrings.get(s);
    }

    private String getRandomCharacter(String s){
        int ind = rand.nextInt(s.length());
        return Character.toString(s.charAt(ind));
    }
}

class LOCCode {
    String code, locClass, locSubclass;
    int start, end;

    public LOCCode(String c, String lcc, String lcsc, int s, int e){
        code = c;
        locClass = lcc;
        locSubclass = lcsc;
        start = s;
        end = e;
    }

    @Override
    public String toString(){
        return code+" ("+locClass+", "+locSubclass+") ["+start+", "+end+"]";
    }
}
