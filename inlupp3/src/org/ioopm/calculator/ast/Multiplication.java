package org.ioopm.calculator.ast;

public class Multiplication extends Binary {
    public Multiplication(SymbolicExpression v1, SymbolicExpression v2){
        super(v1, v2);
    }

    public String getName(){
        return "*";
    }

    public int getPriority(){
        return 100;
    }
}
