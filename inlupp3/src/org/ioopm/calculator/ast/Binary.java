package org.ioopm.calculator.ast;

public abstract class Binary extends SymbolicExpression {
    private SymbolicExpression l;
    private SymbolicExpression r;

    public Binary(SymbolicExpression l, SymbolicExpression r){
        this.l = l;
        this.r = r;
    }
}
