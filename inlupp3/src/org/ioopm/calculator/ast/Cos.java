package org.ioopm.calculator.ast;

public class Cos extends Unary {
     public Cos(SymbolicExpression value){
        super(value);
    }
     
    public String getName(){
        return "cos";
    }

    public SymbolicExpression eval(Environment vars) {
    SymbolicExpression a = this.getArg();
    SymbolicExpression arg = a.eval(vars);
    if (arg.isConstant()) {
        return new Constant(Math.cos(arg.getValue()));
    } else {
        return new Cos(arg);
    }
}
}
