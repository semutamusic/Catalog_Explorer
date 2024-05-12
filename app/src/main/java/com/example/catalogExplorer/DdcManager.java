package com.example.catalogExplorer;

import android.content.Context;
import android.text.InputType;

import java.util.Random;

public class DdcManager extends CatalogManager<DdcNode>{
    private static DdcManager instance;

    public static DdcManager getInstance(Context c){
        if(instance == null){
            instance = new DdcManager(c);
        }
        return instance;
    }

    private DdcManager(Context context){
        super(
                context,
                new DdcNode(new DdcCode(), new DdcCode(),"Root"),
                1,
                R.raw.dewey_complete,
                R.raw.helptext_ddc
        );
        //printTree();
    }

    @Override
    protected DdcNode csvLineToNode(String[] line){
        DdcCode newStart = new DdcCode(new DdcClass(line[0]));
        DdcCode newEnd = new DdcCode(new DdcClass(line[1]));
        return new DdcNode(newStart, newEnd, line[2]);
    }

    @Override
    public CatalogNode[] getGuessGameChoices() {
        return getGuessGameChoices(1);
    }

    @Override
    public CatalogCode getNewCode(String[] fields) {
        String[] filteredFields = new String[4];
        for(int i = 0; i < 4; i++){
            filteredFields[i] = fields.length > i ? fields[i] : "";
        }

        return new DdcCode(
                new DdcClass(filteredFields[0]),
                new DdcDecimal(filteredFields[1]),
                new CutterNumber(filteredFields[2]),
                new Year(filteredFields[3])
        );
    }
}

class DdcNode extends CatalogNode<DdcCode>{
    public DdcNode(DdcCode start, DdcCode end, String desc){
        codeStart = start;
        codeEnd = end;
        description = desc;
    }

    @Override
    public boolean isInside(CatalogNode that) {
        if(this.codeEnd.isNull()){
            return this.codeStart.isInside(that);
        }
        return this.codeStart.isInside(that) && this.codeEnd.isInside(that);
    }

    @Override
    public DdcCode getRandomCode(Random rand) {
        return new DdcCode(
                (DdcClass) codeStart.getFields()[0],
                new DdcDecimal(rand),
                new CutterNumber(rand),
                new Year(rand)
        );
    }

    @Override
    public String getGuessGameHint() {
        return codeStart.toString();
    }
    @Override
    public String getGuessGameChoice() {
        return description;
    }

    @Override
    public String toString() {
        return codeStart.toString() + " [" + description + "]";
    }
}

class DdcCode extends CatalogCode<DdcCode>{
    public DdcCode(DdcClass dc, DdcDecimal dd, CutterNumber cn, Year y){
        super(4);
        fields[0] = dc;
        fields[1] = dd;
        fields[2] = cn;
        fields[3] = y;
    }

    public DdcCode(DdcClass dc){ this(dc, new DdcDecimal(), new CutterNumber(), new Year()); }

    public DdcCode(){
        this(new DdcClass(), new DdcDecimal(), new CutterNumber(), new Year());
    }

    @Override
    public boolean isInside(CatalogNode cn) {
        if(isNull()){
            return false;
        }
        DdcClass thisClass = (DdcClass) fields[0];
        DdcClass cnStartClass = (DdcClass) cn.codeStart.fields[0];
        DdcClass cnEndClass = (DdcClass) cn.codeEnd.fields[0];
        if(cn.codeEnd.isNull()){
            return thisClass.compareTo(cnStartClass) == 0;
        }
        return thisClass.compareTo(cnStartClass) >= 0 && thisClass.compareTo(cnEndClass) <= 0;
    }

    @Override
    public String toString() {
        String result = "";

        if(!fields[0].isNull()){
            result += fields[0];
        }
        if(!fields[1].isNull()){
            result += fields[1];
        }
        if(!fields[2].isNull()){
            result += " " + fields[2];
        }
        if(!fields[3].isNull()){
            result += " " + fields[3];
        }
        return result;
    }

    @Override
    public int compareTo(DdcCode that) {
        for(int i = 0; i < fields.length; i++){
            int comparison = this.fields[i].compareTo(that.fields[i]);
            if(comparison != 0){
                return comparison;
            }
        }
        return 0;
    }
}

class DdcClass extends CatalogField<DdcClass>{
    public DdcClass(){ super("Class", InputType.TYPE_CLASS_NUMBER); }

    public DdcClass(String c){
        this();
        try{
            Integer.parseInt(c);
            if(c.length() > 3){
                c = c.substring(0, 3);
            }
            while(c.length() < 3){
                c = '0' + c;
            }
            value = c;
        }
        catch(NumberFormatException ignored){ }
    }

    @Override
    public int compareTo(DdcClass that){
        if(this.isNull() || that.isNull()){ return 0; }
        for(int i = 0; i < 3; i++){
            char classThis = this.value.charAt(i);
            char classThat = that.value.charAt(i);
            if(classThis != classThat){
                return classThis - classThat;
            }
        }
        return 0;
    }
}

class DdcDecimal extends CatalogField<DdcDecimal>{
    public DdcDecimal(){ super("Decimal", InputType.TYPE_CLASS_NUMBER); }

    public DdcDecimal(String d){
        this();
        try{
            Float.parseFloat(d);
            value = d;
        }
        catch(NumberFormatException ignored){ }
    }

    public DdcDecimal(Random rand){
        this();
        String decimal = Float.toString(rand.nextFloat());
        value = decimal.substring(1, rand.nextInt(3) + 2);
        if(value.equals(".") || value.equals(".0") || value.equals(".00")){
            value = "";
        }
    }

    @Override
    public int compareTo(DdcDecimal that) {
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
