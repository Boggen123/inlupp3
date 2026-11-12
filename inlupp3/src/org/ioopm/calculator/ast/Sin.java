package org.ioopm.calculator.ast;

public class Sin extends Unary {
     public Sin(SymbolicExpression value){
        super(value);
    }

    public String getName(){
        return "sin";
    }

    public SymbolicExpression eval() {
    SymbolicExpression arg = this.arg.eval();
    if (arg.isConstant()) {
        return new Constant(Math.sin(arg.getValue()));
    } else {
        return new Sin(arg);
    }
}

}