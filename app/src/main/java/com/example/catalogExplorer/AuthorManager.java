package com.example.catalogExplorer;

import android.content.Context;
import android.text.InputType;

import java.util.Random;

public class AuthorManager extends CatalogManager<AuthorNode>{

    private static AuthorManager instance;

    public static AuthorManager getInstance(Context context){
        if(instance == null){
            instance = new AuthorManager(context);
        }
        return instance;
    }

    private AuthorManager(Context context){
        super(
                context,
                new AuthorNode("Root"),
                2,
                R.raw.authors_complete,
                R.raw.helptext_auth
        );
        //printTree();
    }

    @Override
    protected AuthorNode csvLineToNode(String[] line){
        if(line[0].length() > 0) {
            String name = line[0].substring(0, 1);
            root.addChildNode(new AuthorNode(name));
        }
        if(line[0].length() > 1) {
            String name = line[0].substring(0, 2);
            root.addChildNode(new AuthorNode(name));
        }
        return new AuthorNode(line[0], line[1]);
    }

    @Override
    public CatalogNode[] getGuessGameChoices() {
        return getGuessGameChoices(2);
    }

    @Override
    public CatalogCode getNewCode(String[] fields){
        String lastName = fields.length > 0 ? fields[0] : "";
        String firstName = fields.length > 1 ? fields[1] : "";

        return new AuthorCode(
                new AuthorLastName(lastName),
                new AuthorFirstName(firstName)
        );
    }
}

class AuthorNode extends CatalogNode<AuthorCode>{
    boolean isFullName;
    public AuthorNode(String ln, String fn){
        codeStart = new AuthorCode(
                new AuthorLastName(ln),
                new AuthorFirstName(fn)
        );
        codeEnd = new AuthorCode();
        description = fn.length() > 0 ? ln + ", " + fn : ln;
        isFullName = true;
    }

    public AuthorNode(String label){
        codeStart = new AuthorCode(
                new AuthorLastName(label),
                new AuthorFirstName()
        );
        codeEnd = new AuthorCode();
        description = label;
        isFullName = false;
    }

    public boolean isFullName(){
        return isFullName;
    }

    @Override
    public void addChildNode(CatalogNode childNode) {
        if(this.compareTo(childNode) == 0){
            return;
        }
        for(int i = 0; i < subnodes.size(); i++){
            AuthorNode currNode = (AuthorNode)subnodes.get(i);
            if(childNode.isInside(currNode) && !currNode.isFullName()){
                currNode.addChildNode(childNode);
                return;
            }
        }
        subnodes.add(childNode);
    }

    @Override
    public boolean isInside(CatalogNode that) {
        return this.codeStart.matches((AuthorCode) that.codeStart);
    }

    @Override
    public AuthorCode getRandomCode(Random rand) {
        return codeStart;
    }

    @Override
    public String getGuessGameHint() {
        return codeStart.getFields()[0].toString();
    }
    @Override
    public String getGuessGameChoice(){
        return codeStart.getFields()[1].toString();
    }

    @Override
    public String toString(){
        String result = codeStart.toString();
        if(!isFullName) {
            result += "...";
        }
        return result;
    }
}

class AuthorCode extends CatalogCode<AuthorCode>{
    public AuthorCode(AuthorLastName ln, AuthorFirstName fn){
        super(2);
        fields[0] = ln;
        fields[1] = fn;
    }
    public AuthorCode(){
        this(new AuthorLastName(), new AuthorFirstName());
    }

    @Override
    public boolean matches(AuthorCode that){
        return this.fields[0].toString().startsWith(that.fields[0].toString());
    }

    @Override
    public boolean isInside(CatalogNode cn) {
        return this.fields[0].toString().startsWith(cn.codeStart.getFields()[0].toString());
    }

    @Override
    public String toString() {
        String result = fields[0].getValue();
        if(!fields[1].isNull()){
            result += ", " + fields[1].getValue();
        }
        return result;
    }

    @Override
    public int compareTo(AuthorCode that) {
        if(this.fields[0].compareTo(that.fields[0]) != 0){
            return this.fields[0].compareTo(that.fields[0]);
        }
        return this.fields[1].compareTo(that.fields[1]);
    }
}

class AuthorLastName extends CatalogField<AuthorLastName>{
    public AuthorLastName(){ super("Last Name", InputType.TYPE_TEXT_FLAG_CAP_WORDS); }

    public AuthorLastName(String n){
        this();
        value = n;
    }
    @Override
    public int compareTo(AuthorLastName that) {
        return this.value.compareTo(that.value);
    }
}

class AuthorFirstName extends CatalogField<AuthorFirstName>{
    public AuthorFirstName() { super("First Name", InputType.TYPE_TEXT_FLAG_CAP_WORDS); }

    public AuthorFirstName(String n){
        this();
        value = n;
    }

    @Override
    public int compareTo(AuthorFirstName that) { return this.value.compareTo(that.value); }
}
