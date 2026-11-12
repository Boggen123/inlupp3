package org.ioopm.calculator.ast;

public abstract class Unary extends SymbolicExpression {
        private SymbolicExpression value;


    public Unary(SymbolicExpression value){
        this.value = value;
    }
}
