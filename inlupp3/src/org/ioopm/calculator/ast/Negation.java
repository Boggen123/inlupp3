package org.ioopm.calculator.ast;

public class Negation extends Unary {
     public Negation(SymbolicExpression value){
        super(value);
    }

    public String getName(){
        return "-";
    }

    public SymbolicExpression eval(Environment vars) {
    SymbolicExpression a = this.getArg();
    SymbolicExpression arg = a.eval(vars);
        if (arg.isConstant()) {
            return new Constant(-arg.getValue());
        } else {
            return new Negation(arg);
        }
    }
}