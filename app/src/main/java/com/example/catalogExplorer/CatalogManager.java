package com.example.catalogExplorer;

import android.content.Context;
import android.text.InputType;
import android.util.Log;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

abstract class CatalogManager<T extends CatalogNode>{
    protected T root;
    protected Random rand;
    protected final String NAME, HELP;

    protected CatalogManager(Context context, T rootNode, int nameIndex, int csvId, int helpId){
        NAME = context.getResources().getStringArray(R.array.main_manager_choices)[nameIndex];
        HELP = readHelpText(context, helpId);
        rand = new Random();

        //Read in CSV
        root = rootNode;
        try(InputStream is = context.getResources().openRawResource(csvId);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            CSVReader reader = new CSVReaderBuilder(br).withSkipLines(1).build() ){
            String[] nextLine;
            int counter = 0;
            while((nextLine = reader.readNext()) != null){
                //Read codes into map
                CatalogNode newNode = csvLineToNode(nextLine);
                root.addChildNode(newNode);
                counter++;
            }
            Log.i(NAME + " CSV Read", counter+" lines read");
        }
        catch(Exception e){ Log.e(NAME + " CSV Read Error", e.toString()); }
    }

    public T getRoot(){ return root; }
    public String getManagerName(){ return NAME; }
    public CatalogField[] getFields(){
        return root.codeStart.getFields();
    }
    public ArrayList<String> getDescriptionTree(CatalogCode cc){
        ArrayList<String> result = new ArrayList<>();
        T currNode = root;
        while(currNode.subnodes.size() > 0){
            T nextNode = currNode;
            for(int i = 0; i < currNode.subnodes.size(); i++){
                if(cc.isInside((T) currNode.subnodes.get(i))){
                    nextNode = (T) currNode.subnodes.get(i);
                }
            }
            if(nextNode == currNode){
                break;
            }
            currNode = nextNode;
            result.add(currNode.getDescription());
        }
        return result;
    }

    //Get random code
    public CatalogCode getRandomCode(){
        return getRandomCodeBelow(root);
    }
    private CatalogCode getRandomCodeBelow(T cn){
        T node = getRandomNodeBelow(cn);
        return node.getRandomCode(rand);
    }
    private T getRandomNodeBelow(T cn){
        return getRandomNodeBelow(cn, Integer.MAX_VALUE);
    }
    private T getRandomNodeBelow(T cn, int depth){
        if(cn.getSubnodes().isEmpty() || depth <= 0){
            return cn;
        }
        int nextInd = rand.nextInt(cn.getSubnodes().size());
        return getRandomNodeBelow((T)cn.getSubnodes().get(nextInd), --depth);
    }

    //Search for nodes by keywords
    public ArrayList<T> getNodesByKeyword(String[] keywords){
        for(int i = 0; i < keywords.length; i++){
            keywords[i] = keywords[i].toLowerCase();
        }
        return getNodesByKeyword(keywords, root, "");
    }
    private ArrayList<T> getNodesByKeyword(String[] keywords, T node, String descriptions){
        ArrayList<T> results = new ArrayList<>();

        String lowerCaseDescription = node.getDescription().toLowerCase();
        descriptions += " " + lowerCaseDescription;

        if(doAllKeywordsMatch(keywords, descriptions) && doAnyKeywordsMatch(keywords, lowerCaseDescription)) {
            results.add(node);
        }
        for(int i = 0; i < node.subnodes.size(); i++){
            results.addAll(getNodesByKeyword(keywords, (T)node.subnodes.get(i), descriptions));
        }

        return results;
    }
    private boolean doAllKeywordsMatch(String[] keywords, String descriptions){
        for(String keyword : keywords){
            if(!descriptions.contains(keyword)){
                return false;
            }
        }
        return true;
    }
    private boolean doAnyKeywordsMatch(String[] keywords, String descriptions){
        for(String keyword : keywords){
            if(descriptions.contains(keyword)){
                return true;
            }
        }
        return false;
    }

    //Get text for Help
    public String getHelpText(){ return HELP; }
    private String readHelpText(Context context, int resId){
        String helpText = "";
        try {
            InputStream is = context.getResources().openRawResource(resId);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while((line = br.readLine()) != null){
                helpText += line + '\n';
            }
        }
        catch(Exception e){ Log.e("Help Read Error", e.toString()); }
        return helpText;
    }

    //Get three appropriate nodes for Guess Game
    protected CatalogNode[] getGuessGameChoices(int depth){
        CatalogNode[] choices = new CatalogNode[3];

        CatalogNode topNode;
        do {
            topNode = getRandomNodeBelow(root, depth);
        }
        while(topNode.getUniqueGuessGameSubnodeCount() < 3);

        for(int i = 0; i < choices.length; i++){
            while(choices[i] == null){
                CatalogNode randomNode = (CatalogNode) topNode.subnodes.get(rand.nextInt(topNode.subnodes.size()));
                if(!doesArrayContainGuess(choices, randomNode)){
                    choices[i] = randomNode;
                }
            }
        }
        return choices;
    }
    private boolean doesArrayContainGuess(CatalogNode[] arr, CatalogNode node){
        for(CatalogNode arrNode : arr){
            if(arrNode != null && node.getGuessGameChoice().equals(arrNode.getGuessGameChoice())){
                return true;
            }
        }
        return false;
    }

    //Print node tree
    public void printTree() {
        printTree(root, 0);
    }
    private void printTree(T node, int depth){
        String spacer = "";
        for(int i = 0; i < depth; i++){
            spacer += "-";
        }
        Log.i(NAME + " Print", spacer + node.toString());
        for(int i = 0; i < node.getSubnodes().size(); i++){
            printTree((T)node.getSubnodes().get(i), depth + 1);
        }
    }

    //Abstract methods
    abstract protected CatalogNode csvLineToNode(String[] line);
    abstract public CatalogNode[] getGuessGameChoices();
    abstract public CatalogCode getNewCode(String[] fields);
}
abstract class CatalogNode<T extends CatalogCode> implements Comparable<CatalogNode>{
    protected T codeStart, codeEnd;
    protected String description;
    protected List<CatalogNode> subnodes = new ArrayList<>();

    public String getDescription(){ return description; }
    public T getCodeStart(){ return codeStart; }
    public T getCodeEnd(){ return codeEnd; }
    public List<CatalogNode> getSubnodes(){ return subnodes; }
    public int getUniqueGuessGameSubnodeCount(){
        ArrayList<String> uniqueHints = new ArrayList<>();
        for(CatalogNode cn : subnodes){
            if(!uniqueHints.contains(cn.getGuessGameHint())){
                uniqueHints.add(cn.getGuessGameHint());
            }
        }
        return uniqueHints.size();
    }
    public void addChildNode(CatalogNode childNode){
        for(CatalogNode cn : subnodes){
            if(childNode.isInside(cn)){
                cn.addChildNode(childNode);
                return;
            }
        }
        subnodes.add(childNode);
        Collections.sort(subnodes);
    }
    @Override
    public int compareTo(CatalogNode that){
        if(this.codeStart.compareTo(that.codeStart) != 0){
            return this.codeStart.compareTo(that.codeStart);
        }
        return this.codeEnd.compareTo(that.codeEnd);
    }

    //Abstract methods
    abstract public boolean isInside(CatalogNode that);
    abstract public T getRandomCode(Random rand);
    abstract public String getGuessGameHint();
    abstract public String getGuessGameChoice();
    abstract public String toString();
}

abstract class CatalogCode<T extends CatalogCode> implements Comparable<T>{
    protected CatalogField[] fields;
    public CatalogCode(int fieldNumber) { fields = new CatalogField[fieldNumber]; }
    public CatalogField[] getFields(){
        return fields;
    }

    public boolean isNull(){
        for(CatalogField cf : fields){
            if(!cf.isNull()){
                return false;
            }
        }
        return true;
    }

    public boolean matches(T that){
        for(int i = 0; i < fields.length; i++){
            CatalogField thisField = this.fields[i];
            CatalogField thatField = that.fields[i];
            if(!thatField.isNull() && thisField.compareTo(thatField) != 0){
                return false;
            }
        }
        return true;
    }

    //Abstract methods
    abstract public boolean isInside(CatalogNode cn);
    abstract public String toString();
}

abstract class CatalogField<T extends CatalogField> implements Comparable<T>{
    protected String label;
    protected String value;
    protected int keyboardType;

    public CatalogField(String n, int kt){
        label = n;
        keyboardType = kt;
        value = "";
    }

    public boolean isNull(){
        return value.length() == 0;
    }
    public String getLabel() { return label; }
    public int getKeyboardType(){ return keyboardType; }
    public String getValue(){
        return value;
    }

    @Override
    public String toString(){
        return value;
    }
}

class CutterNumber extends CatalogField<CutterNumber>{
    public CutterNumber(){
        super("Cutter Number", InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
    }

    public CutterNumber(String cl, String cn){
        this();
        if(cl.length() + cn.length() > 0){
            value = "." + cl + cn;
        }
    }

    public CutterNumber(String c){
        this();
        value = c;
    }

    public CutterNumber(Random rand){
        this(new CutterNumber(), new CutterNumber(), rand);
    }
    //Returns Cutter Number between the two arguments
    public CutterNumber(CutterNumber cn1, CutterNumber cn2, Random rand){
        this();
        if(cn1.isNull() || cn2.isNull()) {
            if (cn1.isNull() && cn2.isNull()) {
                value = ".";
                value += getRandomLetter(rand);
                value += getRandomNumber(rand);
                value += getRandomNumber(rand);
                if (rand.nextInt(3) == 0) {
                    value += getRandomNumber(rand);
                }
            }
            else if (cn2.isNull()) {
                value = cn1.value;
            }
            else{
                value = cn2.value;
            }
        }
        else {
            value = ".";
            for (int i = 1; i < Math.min(cn1.value.length(), cn2.value.length()); i++) {
                value += getRandomCharBetween(cn1.value.charAt(i), cn2.value.charAt(i), rand);
            }
        }
    }

    private char getRandomLetter(Random rand){
        return getRandomCharBetween('A', 'Z', rand);
    }
    private char getRandomNumber(Random rand){
        return getRandomCharBetween('2', '9', rand);
    }
    private char getRandomCharBetween(char c1, char c2, Random rand){
        if(c1 == c2){
            return c1;
        }
        if(c1 > c2){
            return (char)(rand.nextInt(c1-c2)+c2);
        }
        return (char)(rand.nextInt(c2-c1)+c1);
    }
    @Override
    public int compareTo(CutterNumber that){
        for(int i = 0; i < Math.min(this.value.length(), that.value.length()); i++){
            if(this.value.charAt(i) != that.value.charAt(i)){
                return Character.compare(this.value.charAt(i), that.value.charAt(i));
            }
        }
        int thisLength = this.value.length();
        int thatLength = that.value.length();
        return thisLength < thatLength ? -1 : thisLength > thatLength ? 1 : 0;
    }
}

class Year extends CatalogField<Year>{
    public Year(){
        super("Year", InputType.TYPE_CLASS_NUMBER);
    }
    public Year(String y) {
        this();
        try{
            Integer.parseInt(y);
            value = y;
        }
        catch(NumberFormatException ignored){ }
    }
    public Year(Random rand){
        this(new Year(), new Year(), rand);
    }
    public Year(Year y1, Year y2, Random rand){
        this();
        if(y1.isNull() || y2.isNull()) {
            if (y1.isNull() && y2.isNull()) {
                value = String.valueOf(rand.nextInt(2024-1900) + 1900);
            }
            else if (y2.isNull()) {
                value = y1.value;
            }
            else{
                value = y2.value;
            }
        }
        else {
            int year1 = Integer.parseInt(y1.value);
            int year2 = Integer.parseInt(y2.value);
            int newYear;

            if (year1 == year2) {
                newYear = year1;
            } else if (year1 > year2) {
                newYear = rand.nextInt(year1 - year2) + year2;
            } else {
                newYear = rand.nextInt(year2 - year1) + year1;
            }
            value = String.valueOf(newYear);
        }
    }

    @Override
    public int compareTo(Year that){
        if(this.isNull() && that.isNull()){ return 0; }
        if(that.isNull()){ return 1; }
        if(this.isNull()){ return -1; }

        int thisYear = Integer.parseInt(this.value);
        int thatYear = Integer.parseInt(that.value);

        return Integer.compare(thisYear, thatYear);
    }
}
