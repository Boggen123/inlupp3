package org.ioopm.calculator.ast;

public class Division extends Binary {
    public Division(SymbolicExpression t, SymbolicExpression n) {
    super(t, n);
    }

    public String getName(){
        return "/";
    }

    public int getPriority(){
        return 100;
    }
}
