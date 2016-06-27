package org.yesworkflow.cli;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by thsong on 5/18/16.
 */
public class ASPRule {
    String predicateName = "";
    ArrayList<String> literals = new ArrayList<>();
//    int ruleType = 1;

    public ASPRule ParseFromString (String ruleAsString) {
        try {
//            Matcher m = Pattern.compile("\\(([^)]+)\\)").matcher(ruleAsString);
//            int splitPoint = ruleAsString.indexOf("\\(");
//            String[] ss = ruleAsString.split("\\(");
//            setPredicateName(ruleAsString.substring(0, splitPoint));
//            String literalsString = ruleAsString.substring(splitPoint+1, ruleAsString.lastIndexOf("\\)"));
            String predicateName = ruleAsString.split("\\(")[0];
            setPredicateName(predicateName);
            String literalsString = ruleAsString.substring(predicateName.length()+1, ruleAsString.length()-1);
            for (String item : literalsString.split(",")) addLiteral(item.replace("\"", "").trim());
            return this;
        } catch (NullPointerException e) {
            System.out.println("Cannot formet :\"" + ruleAsString + "\" as a ASP rule");
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<String> getLiterals () {
        return literals;
    }

    public ASPRule setPredicateName(String predicateName){
        this.predicateName = predicateName;
        return this;
    }

    public ASPRule addLiteral (String literalName) {
        if (literalName == null || literalName.isEmpty()) {
            System.out.println("WorkflowSpec: empty literal name.");
            return null;
        }
        literals.add("\"" + literalName + "\"");
        return this;
    }

    public ASPRule addLiterals (String... literalNames) {
        if (literalNames == null || literalNames.length == 0) {
            System.out.println("WorkflowSpec: empty literal names.");
            return null;
        }
        for (String item : literalNames) literals.add("\"" + item + "\"");
        return this;
    }

//    public ASPRule setAsFact() {ruleType = 1; return this;} // fact
//    public ASPRule setAsRule() {ruleType = 2; return this;} // rule
//    public ASPRule setAsIC() {ruleType = 3; return this;}   // integrity constraint

    public String toString(){
        String literalsString = String.join(", ", literals);
        return String.format("%s(%s).", predicateName, literalsString);
//        if(ruleType == 1) return String.format("(%s).", literalsString);
//        else if(ruleType == 2) return String.format("%s :- (%s).", predicateName, literalsString);
//        else if(ruleType == 3) return String.format(":- (%s).", literalsString);
//        else System.out.println("wrong rule type: " + ruleType); return "";
    }
}
