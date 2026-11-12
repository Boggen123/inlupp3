package org.ioopm.calculator.ast;

public class Log extends Unary {
     public Log(SymbolicExpression value){
        super(value);
    }

    public String getName(){
        return "log";
    }

    public SymbolicExpression eval(Environment vars) {
    SymbolicExpression a = this.getArg();
    SymbolicExpression arg = a.eval(vars);
    if (arg.isConstant()) {
        return new Constant(Math.log(arg.getValue()));
    } else {
        return new Log(arg);
    }
}
}
