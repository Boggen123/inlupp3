package org.ioopm.calculator.ast;

public class Assignment extends Binary {
        public Assignment(SymbolicExpression l, SymbolicExpression r){
        super(l, r);
    }

    public String getName(){
        return "=";
    }

    public int getPriority(){
        return 10;
    }

//TODO: Ändra, jättefel
    public SymbolicExpression eval(Environment vars) {
        SymbolicExpression value = this.getLhs().eval(vars);
        SymbolicExpression target = this.getRhs();

    }
}
