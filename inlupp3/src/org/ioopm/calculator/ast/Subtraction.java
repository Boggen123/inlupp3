package org.ioopm.calculator.ast;

public class Subtraction extends Binary {
    public Subtraction(SymbolicExpression l, SymbolicExpression r) {
        super(l, r);
    }

    public String getName(){
        return "-";
    }

    public int getPriority(){
        return 50;
    }
}
