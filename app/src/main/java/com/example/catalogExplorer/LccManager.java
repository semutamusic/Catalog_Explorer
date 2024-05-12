package com.example.catalogExplorer;

import android.content.Context;
import android.text.InputType;
import android.util.Log;

import java.util.Random;

public class LccManager extends CatalogManager<LccNode>{
    private static LccManager instance;

    public static LccManager getInstance(Context c){
        if(instance == null){
            instance = new LccManager(c);
        }
        return instance;
    }

    private LccManager(Context context){
        super(
                context,
                new LccNode(new LccCode(), new LccCode(), "Root"),
                0,
                R.raw.lcc_complete,
                R.raw.helptext_lcc
        );
        //printTree();
    }

    @Override
    protected LccNode csvLineToNode(String[] line){
        LccCode newStart, newEnd;
        newStart = new LccCode(
                new LccClass(line[0]),
                new LccSubdivision(line[1], line[2]),
                new CutterNumber(line[3], line[4]),
                new CutterNumber(),
                new Year()
        );
        newEnd = new LccCode(
                new LccClass(
                        line[5].equals("") ? line[0] : line[5]
                ),
                new LccSubdivision(
                        line[6].equals("") ? line[1] : line[6],
                        line[7].equals("") ? line[2] : line[7]
                ),
                new CutterNumber(
                        line[8].equals("") ? line[3] : line[8],
                        line[9].equals("") ? line[4] : line[9]
                ),
                new CutterNumber(),
                new Year()
        );
        return new LccNode(newStart, newEnd, line[10]);
    }

    @Override
    public CatalogNode[] getGuessGameChoices(){ return getGuessGameChoices(1); }

    @Override
    public CatalogCode getNewCode(String[] fields){
        String[] filteredFields = new String[5];
        for(int i = 0; i < 5; i++){
            filteredFields[i] = fields.length > i ? fields[i] : "";
        }

        return new LccCode(
                new LccClass(filteredFields[0]),
                new LccSubdivision(filteredFields[1]),
                new CutterNumber(filteredFields[2]),
                new CutterNumber(filteredFields[3]),
                new Year(filteredFields[4])
        );
    }
}

class LccNode extends CatalogNode<LccCode>{
    public LccNode(LccCode start, LccCode end, String desc){
        codeStart = start;
        codeEnd = end;
        description = desc;
    }

    @Override
    public boolean isInside(CatalogNode that){
        if(this.codeStart.compareTo(this.codeEnd) == 0){
            return this.codeStart.isInside(that);
        }
        return this.codeStart.isInside(that) && this.codeEnd.isInside(that);
    }

    @Override
    public LccCode getRandomCode(Random rand){
        if(codeEnd.isNull()){
            return new LccCode(codeStart, codeStart, rand);
        }
        return new LccCode(codeStart, codeEnd, rand);
    }

    @Override
    public String getGuessGameHint(){
        String result = codeStart.toString();
        result += codeEnd.isNull() ? "" : " - " + codeEnd.toString();
        return result;
    }
    @Override
    public String getGuessGameChoice(){
        return description;
    }

    @Override
    public String toString(){
        String result = codeStart.toString();
        if(!codeEnd.getFields()[0].toString().contains("_")){
            result += " -";
            for(int i = 0; i < codeStart.getFields().length; i++){
                if(codeStart.getFields()[i].compareTo(codeEnd.getFields()[i]) != 0){
                    result += " " + codeEnd.getFields()[i];
                }
            }
        }
        result += " [" + description + "]";
        return result;
    }
}

class LccCode extends CatalogCode<LccCode>{
    public LccCode(LccClass lc, LccSubdivision s, CutterNumber c1, CutterNumber c2, Year y){
        super(5);
        fields[0] =  lc;
        fields[1] = s;
        fields[2] = c1;
        fields[3] = c2;
        fields[4] = y;
    }

    public LccCode(){
        this(new LccClass(), new LccSubdivision(), new CutterNumber(), new CutterNumber(), new Year());
    }

    public LccCode(LccCode lc1, LccCode lc2, Random rand){
        this(
                (LccClass) lc1.fields[0],
                new LccSubdivision((LccSubdivision) lc1.fields[1], (LccSubdivision) lc2.fields[1], rand),
                new CutterNumber((CutterNumber) lc1.fields[2], (CutterNumber) lc2.fields[2], rand),
                new CutterNumber((CutterNumber) lc1.fields[3], (CutterNumber) lc2.fields[3], rand),
                new Year((Year) lc1.fields[4], (Year) lc2.fields[4], rand)
        );

        if(lc1.fields[0].compareTo(lc2.fields[0]) != 0){
            Log.w("LccCode Warning", "Random codes should have equivalent classes");
        }
    }

    @Override
    public boolean isInside(CatalogNode cn){
        if(this.isNull()){
            return false;
        }
        if(cn.getCodeEnd().isNull()){
            return this.matches((LccCode) cn.getCodeStart());
        }
        return this.matches((LccCode) cn.getCodeStart()) ||
                this.matches((LccCode) cn.getCodeEnd()) ||
                (this.compareTo((LccCode) cn.getCodeStart()) >= 0 &&
                        this.compareTo((LccCode) cn.getCodeEnd()) <= 0);
    }

    @Override
    public String toString(){
        String result = "";

        if(!fields[0].isNull()){
            result += fields[0];
        }
        if(!fields[1].isNull()){
            result += " " + fields[1];
        }
        if(!fields[2].isNull()){
            result += " " + fields[2];
        }
        if(!fields[3].isNull()) {
            result += " ";
            if (fields[2].isNull()) {
                result += fields[3];
            } else {
                result += fields[3].getValue().substring(1);
            }
        }
        if(!fields[4].isNull()){
            result += " " + fields[4];
        }
        return result;
    }

    @Override
    public int compareTo(LccCode that){
        for(int i = 0; i < fields.length; i++){
            int comparison = this.fields[i].compareTo(that.fields[i]);
            if(comparison != 0){
                return comparison;
            }
        }
        return 0;
    }
}

class LccClass extends CatalogField<LccClass>{
    public LccClass(){
        super("Class", InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
    }
    public LccClass(String c){
        this();
        value = c;
    }

    @Override
    public int compareTo(LccClass that){
        for(int i = 0; i < Math.min(this.value.length(), that.value.length()); i++){
            if(this.value.charAt(i) != that.value.charAt(i)){
                return (int)this.value.charAt(i) - (int)that.value.charAt(i);
            }
        }
        int thisLength = this.value.length();
        int thatLength = that.value.length();
        return thisLength < thatLength ? -1 : thisLength > thatLength ? 1 : 0;
    }
}

class LccSubdivision extends CatalogField<LccSubdivision>{
    public LccSubdivision(){
        super("Subdivision", InputType.TYPE_CLASS_NUMBER);
    }
    public LccSubdivision(String i, String d){
        this();
        value = i;
        if(d.length() > 0){
            value += "." + d;
        }
        try{
            Float.parseFloat(value);
        }
        catch(NumberFormatException e){
            value = "";
        }
    }
    public LccSubdivision(String s){
        this();
        try{
            Float.parseFloat(s);
            value = s;
        }
        catch(NumberFormatException ignored){ }
    }
    public LccSubdivision(LccSubdivision ls1, LccSubdivision ls2, Random rand){
        this();
        if(ls1.isNull() || ls2.isNull()) {
            if (ls1.isNull() && ls2.isNull()) {
                value = String.valueOf(rand.nextInt(9999));
            }
            else if (ls2.isNull()) {
                value = ls1.value;
            }
            else{
                value = ls2.value;
            }
        }
        else {
            //Get float values of fields
            float sub1 = Float.parseFloat(ls1.value);
            float sub2 = Float.parseFloat(ls2.value);

            //Calculate new float between the given fields
            float newSubdivision;
            if (sub1 == sub2) {
                newSubdivision = sub1;
            } else if (sub1 > sub2) {
                newSubdivision = rand.nextFloat() * (sub1 - sub2) + sub2;
            } else {
                newSubdivision = rand.nextFloat() * (sub2 - sub1) + sub1;
            }

            //Get precision of argument fields
            int decInd1 = ls1.value.indexOf('.');
            int decInd2 = ls2.value.indexOf('.');

            //If both argument fields are ints, format subdivision as whole number
            if (decInd1 == -1 && decInd2 == -1) {
                value = String.valueOf((int) newSubdivision);
            }
            //Otherwise, format subdivision to highest common precision
            else {
                int maxPrecision = Math.max(
                        decInd1 > 0 ? ls1.value.length() - decInd1 : 0,
                        decInd2 > 0 ? ls2.value.length() - decInd2 : 0
                ) - 1;
                String formatString = "%." + maxPrecision + "f";
                value = String.format(formatString, newSubdivision);
            }
        }
    }

    @Override
    public int compareTo(LccSubdivision that){
        if(this.isNull() && that.isNull()){ return 0; }
        if(that.isNull()){ return 1; }
        if(this.isNull()){ return -1; }

        float thisSubdivision = Float.parseFloat(this.value);
        float thatSubdivision = Float.parseFloat(that.value);

        return Float.compare(thisSubdivision, thatSubdivision);
    }
}