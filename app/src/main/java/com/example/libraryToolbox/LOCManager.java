package com.example.libraryToolbox;

import android.content.Context;
import android.util.Log;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LOCManager {
    private static LOCManager instance;
    private Context context;
    private LocClass root;
    private Random rand;

    public static LOCManager getInstance(Context context){
        if(instance == null){
            instance = new LOCManager(context);
        }
        return instance;
    }

    private LOCManager(Context c){
        context = c;
        rand = new Random();
        root = new LocClass("X", "Root");
        try{
            //Get CSV file from res/raw
            InputStream is = context.getResources().openRawResource(R.raw.loc_complete);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            CSVReader reader = new CSVReaderBuilder(br).withSkipLines(1).build();
            //Read in CSV
            String[] nextLine;
            int counter = 0;
            while((nextLine = reader.readNext()) != null){
                //Read codes into map
                LocClassRange newRange = new LocClassRange(
                        nextLine[0], nextLine[9],
                        csvParseFloat(nextLine[1], nextLine[2]),nextLine[3] + nextLine[4],
                        csvParseFloat(nextLine[5], nextLine[6]), nextLine[7] + nextLine[8]
                );
                LocClass currentNode = root;
                //Find LocCode node with subclass
                while(!(currentNode.getSubclass().equals(nextLine[0]))){
                    boolean found = false;
                    for(LocClass subNode : currentNode.getSubclasses()){
                        if(nextLine[0].startsWith(subNode.getSubclass())){
                            currentNode = subNode;
                            found = true;
                            break;
                        }
                    }
                    //If no LocCode found, make a new LocCode
                    if(!found){
                        String newNodeSubclass =
                                nextLine[0].length() <= currentNode.getSubclass().length()
                                        ? nextLine[0]
                                        : nextLine[0].substring(0,currentNode.getSubclass().length()+1);
                        LocClass newNode = new LocClass(newNodeSubclass, nextLine[9]);

                        currentNode.addChildSubclass(newNode);
                        currentNode = newNode;
                    }
                }
                //Add range to LocCode
                currentNode.addRange(newRange);
                counter++;
            }
            Log.i("CSV Read In", counter+" lines read");
            print();
        }
        catch(Exception e){ Log.e("CSV Read Error", e.toString()); }
    }

    private float csvParseFloat(String csv, String csvDec){
        if(csv.equals("")){
            return -1;
        }
        if(csvDec.equals("")){
            return Float.parseFloat(csv);
        }
        return Float.parseFloat(csv+"."+csvDec);
    }

    public LocClass getRoot(){
        return root;
    }

    //Returns description of the LocCode with the first letter of the given code (DAW -> D)
    public String getLOCClass(String code) {
        if(code.equals("")){ return ""; }
        LocClass lc = getNode(code.substring(0,1));
        if(lc != null){
            return lc.getDescription();
        }
        return context.getString(R.string.default_class);
    }

    //Returns description of the LocCode with the given subclass
    public String getLOCDescription(String code) {
        LocClass lsc = getNode(code);
        if(lsc != null){
            return lsc.getDescription();
        }
        return context.getString(R.string.default_subclass);
    }

    //Get a completely random LocCode from the tree
    public LocCode getRandomCode(){
        LocClass node = root;
        //Get initial random node
        int randomInd = rand.nextInt(node.getSubclasses().size());
        node = node.getSubclasses().get(randomInd);
        do {
            //Get a random index from subclasses, including -1
            randomInd = rand.nextInt(node.getSubclasses().size() + 1) - 1;
            //If -1, return this class
            if(randomInd < 0){
                return node.getRandomCode(rand);
            }
            //Otherwise, continue down the tree
            node = node.getSubclasses().get(randomInd);
        }while(node.getSubclasses().size() > 0);
        return node.getRandomCode(rand);
    }
    //Get a random LocCode from the given class
    public LocCode getRandomCode(String locClass){
        LocClass code = getNode(locClass);
        if(code == null){
            Log.i("Random LocCode", "Returning null, "+locClass+" not found");
            return new NullLocCode();
        }
        return code.getRandomCode(rand);
    }
    public LocClass getRandomNodeWithPrefix(String pre, int ttl){
        LocClass currNode;
        if(pre.equals("")){
            currNode = root.getSubclasses().get(rand.nextInt(root.getSubclasses().size()));
        }
        else {
            currNode = getNode(pre);
        }
        if(currNode == null){ return null; }
        ttl -= currNode.getSubclass().length();
        while(ttl > 0 && currNode.getSubclasses().size() > 0){
            currNode = currNode.getSubclasses().get(rand.nextInt(currNode.getSubclasses().size()));
            ttl--;
        }
        return currNode;
    }
    //Returns LocCode node with the argument as the subclass
    private LocClass getNode(String sc){
        LocClass currNode = root;
        boolean foundNext;
        while(!(currNode.getSubclass().equals(sc))){
            foundNext = false;
            for(LocClass lc : currNode.getSubclasses()){
                if(sc.startsWith(lc.getSubclass())){
                    currNode = lc;
                    foundNext = true;
                    break;
                }
            }
            if(!foundNext){
                return null;
            }
        }
        return currNode;
    }

    //Print current LocCode tree
    public void print(){ print(root, 0); }
    private void print(LocClass node, int depth){
        String spacer = "";
        for(int i = 0; i < depth; i++){
            spacer += "-";
        }
        Log.i("LOCManager Print", spacer + node.toString());
        for(LocClass lc : node.getSubclasses()){
            print(lc, depth + 1);
        }
    }
}

//Used as a node in a tree structure containing all LocCodes
//Each LocCode has a List<> of ranges, each with individual descriptions
class LocClass implements Comparable<LocClass>{
    private String subclass, description;
    private List<LocClass> subclasses;
    private List<LocClassRange> ranges;
    private int totalRange_s = Integer.MAX_VALUE, totalRange_e = Integer.MIN_VALUE;

    public LocClass(String sc, String d){
        subclass = sc;
        description = d;

        subclasses = new ArrayList<>();
        ranges = new ArrayList<>();
    }

    //Recursively checks child LocCodes to add argument as child to node of best fit (A -> AB -> ABC)
    public void addChildSubclass(LocClass childNode){
        for(LocClass lc : subclasses){
            if(childNode.subclass.startsWith(lc.subclass)){
                lc.addChildSubclass(childNode);
                return;
            }
        }
        subclasses.add(childNode);
    }

    //Adds range as child to proper parent (1-100 -> 10-50 -> 10-15)
    //Also sets range as mainRange if necessary
    public void addRange(LocClassRange newLcr){
        totalRange_s = Math.min(totalRange_s, (int)newLcr.getSubdivisionStart());
        totalRange_e = Math.max(totalRange_e, (int)newLcr.getSubdivisionEnd());

        for(LocClassRange lcr : ranges){
            LocClassRange inner = lcr.getInnermostSubrangeEncompassing(newLcr);
            if(inner != null){
                inner.subranges.add(newLcr);
                return;
            }
        }
        ranges.add(newLcr);
    }

    public LocCode getRandomCode(Random rand){
        if(totalRange_e - totalRange_s <= 0){
            return new NullLocCode();
        }
        //Choose a range
        LocClassRange lcr = ranges.get(rand.nextInt(ranges.size()));
        while(lcr.subranges.size() > 0){
            lcr = lcr.subranges.get(rand.nextInt(lcr.subranges.size()));
        }
        //Get random subdivision within that range
        float subdivision = lcr.getSubdivisionStart() + rand.nextFloat() * (lcr.getSubdivisionEnd() - lcr.getSubdivisionStart());
        //If Start and End are both ints, round subdivision to a whole number
        if(lcr.getSubdivisionStart() == (int)lcr.getSubdivisionStart() && lcr.getSubdivisionEnd() == (int)lcr.getSubdivisionEnd()){
            subdivision = (float)((int)subdivision);
        }
        return new LocCode(
                subclass,
                subdivision,
                new CutterNumber(rand),
                new CutterNumber(rand),
                1900 + rand.nextInt(2024 - 1900)
        );
    }

    public String getSubclass(){ return subclass; }
    public String getDescription(){ return description; }
    public List<LocClass> getSubclasses(){ return subclasses; }
    public List<LocClassRange> getRanges(){ return ranges; }

    @Override
    public int compareTo(LocClass lc2){
        return subclass.compareTo(lc2.subclass);
    }

    @Override
    public String toString(){
        return subclass + " " + description + " " + totalRange_s + " - " + totalRange_e;
    }
}

//A single range within a LocCode
//Adds detail to broader code ranges
//Has own List<> of subranges
class LocClassRange{
    private String description;
    private LocCode codeStart = new NullLocCode();
    private LocCode codeEnd = new NullLocCode();

    List<LocClassRange> subranges;

    public LocClassRange(String c, String d,
                         float subd_s, String cut_s,
                         float subd_e, String cut_e){
        description = d;

        if(subd_s >= 0) {
            CutterNumber cn = cut_s.equals("") ? new NullCutterNumber() : new CutterNumber(cut_s);
            codeStart = new LocCode(c, subd_s, cn);
        }
        if(subd_e >= 0) {
            CutterNumber cn = cut_e.equals("") ? new NullCutterNumber() : new CutterNumber(cut_e);
            codeEnd = new LocCode(c, subd_e, cn);
        }
        subranges = new ArrayList<>();
    }

    //Returns the subrange that would fit the argument range within it
    public LocClassRange getInnermostSubrangeEncompassing(LocClassRange newLcr){
        for(LocClassRange lcr : subranges){
            if(newLcr.isInside(lcr)){
                return lcr.getInnermostSubrangeEncompassing(newLcr);
            }
        }
        if(newLcr.isInside(this)){
            return this;
        }
        return null;
    }

    //Returns true if this range is within the argument range
    //Used to test hierarchies within ranges
    private boolean isInside(LocClassRange lcr2){
        return (codeStart.compareTo(lcr2.codeStart) >= 0) &&
               (codeEnd.compareTo(lcr2.codeEnd) <= 0);
    }

    public String getDescription(){ return description; }
    public float getSubdivisionStart(){ return codeStart.getSubdivision(); }
    public float getSubdivisionEnd(){ return codeEnd.getSubdivision(); }
    @Override
    public String toString(){
        return description + " " + codeStart
                + (codeEnd.toString().equals("") ? "" : " - ")
                + codeEnd.toString();
    }
}

class LocCode implements Comparable<LocCode>{
    private String locClass;
    private float subdivision;
    private CutterNumber cutter1, cutter2;
    private int year;

    public LocCode(String lc, float s, CutterNumber c1, CutterNumber c2, int y){
        locClass = lc;
        subdivision = s;
        cutter1 = c1;
        cutter2 = c2;
        year = y;
    }
    public LocCode(String lc, float s, CutterNumber c1, CutterNumber c2){
        this(lc, s, c1, c2, -1);
    }
    public LocCode(String lc, float s, CutterNumber c1){
        this(lc, s, c1, new NullCutterNumber(), -1);
    }
    public LocCode(String lc, float s){
        this(lc, s, new NullCutterNumber(), new NullCutterNumber(), -1);
    }

    public String getLocClass(){ return locClass; }
    public float getSubdivision(){ return subdivision; }
    public CutterNumber getCutter1(){ return cutter1; }
    public CutterNumber getCutter2(){ return cutter2; }
    public int getYear(){ return year; }
    @Override
    public String toString(){
        return locClass + " " + subdivision + cutter1 + cutter2 + (year == -1 ? "" : (" " + year));
    }
    @Override
    public int compareTo(LocCode lc2) {
        //Compare classes
        if(!locClass.equals(lc2.locClass)){
            return locClass.compareTo(lc2.locClass);
        }
        //Compare subdivisions
        if(subdivision != lc2.subdivision){
            return (int)(subdivision - lc2.subdivision);
        }
        //Compare Cutter Number 1
        if(cutter1.compareTo(lc2.cutter1) != 0){
            return cutter1.compareTo(lc2.cutter1);
        }
        //Compare Cutter Number 2
        if(cutter2.compareTo(lc2.cutter2) != 0){
            return cutter2.compareTo(lc2.cutter2);
        }
        //Compare years
        if(year < 0){
            if(lc2.year < 0){
                return 0;
            }
            return -1;
        }
        if(lc2.year < 0){
            return 1;
        }
        return year - lc2.year;
    }
}

class NullLocCode extends LocCode{
    public NullLocCode() { super("", -1f); }
    @Override
    public String toString(){ return ""; }
    @Override
    public int compareTo(LocCode lc2){ return 0; }
}

class CutterNumber implements Comparable<CutterNumber>{
    private String cNumber;

    //Returns random Cutter Number
    public CutterNumber(Random rand){
        cNumber = "";
        cNumber += getRandomLetter(rand);
        cNumber += getRandomNumber(rand);
        cNumber += getRandomNumber(rand);
        if(rand.nextInt(3) == 0){ cNumber += getRandomNumber(rand); }
    }

    //Returns set Cutter Number
    public CutterNumber(String cn){
        cNumber = cn;
    }

    //Returns Cutter Number between the two arguments
    public CutterNumber(CutterNumber cn1, CutterNumber cn2){
        Random rand = new Random();
        cNumber = "";
        for(int i = 0; i < Math.min((cn1.cNumber.length()-1), (cn2.cNumber.length())-1); i++) {
            cNumber += getRandomCharBetween(rand, cn1.cNumber.charAt(i), cn2.cNumber.charAt(i));
        }
    }

    private char getRandomLetter(Random rand){
        return getRandomCharBetween(rand, 'A', 'Z');
    }
    private char getRandomNumber(Random rand){
        return getRandomCharBetween(rand, '2', '9');
    }
    private char getRandomCharBetween(Random rand, char c1, char c2){
        return (char)(rand.nextInt(c2-c1)+c1);
    }
    @Override
    public String toString(){
        return "." + cNumber;
    }
    @Override
    public int compareTo(CutterNumber cn2) {
        return cNumber.compareTo(cn2.cNumber);
    }
}

class NullCutterNumber extends CutterNumber{
    public NullCutterNumber() { super(""); }
    @Override
    public String toString(){ return ""; }
    @Override
    public int compareTo(CutterNumber cn2){ return 0; }
}
