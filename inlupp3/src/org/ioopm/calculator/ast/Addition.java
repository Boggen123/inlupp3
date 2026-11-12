package org.ioopm.calculator.ast;

public class Addition extends Binary {
    public Addition(SymbolicExpression l, SymbolicExpression r){
        super(l, r);
    }

    public String getName(){
        return "+";
    }

    public int getPriority(){
        return 50;
    }
}