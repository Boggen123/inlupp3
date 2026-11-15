package org.ioopm.calculator.ast;

import javax.management.RuntimeErrorException;

import org.ioopm.calculator.ast.Variable;

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

//TODO: Ändra, jättefel inte klar ännu
    public SymbolicExpression eval(Environment vars) {
        SymbolicExpression value = this.getLhs().eval(vars);
        SymbolicExpression target = this.getRhs();

        // om högsersidan inte är variabel
        if (!(target instanceof Variable)) {
        	throw new RuntimeException("Right hand side is not variable");
        }

        return value;
    }
}
