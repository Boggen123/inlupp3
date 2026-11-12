package org.ioopm.calculator.ast;

public class Exp extends Unary {
     public Exp(SymbolicExpression value){
        super(value);
    }

    public String getName(){
        return "exp";
    }

    public SymbolicExpression eval() {
    SymbolicExpression arg = this.arg.eval();
    if (arg.isConstant()) {
        return new Constant(Math.exp(arg.getValue()));
    } else {
        return new Exp(arg);
    }
}
}
