package org.ioopm.calculator.ast;

public class Addition extends Binary {
    public Addition(SymbolicExpression l, SymbolicExpression r){
        super(l, r);
    }

    public String getName(){
        return "+";
    }

    public int getPriority(){
        return 50;
    }

    public SymbolicExpression eval(Environment vars) {
        SymbolicExpression left = this.getLhs();
        SymbolicExpression leftEval = left.eval(vars);
        SymbolicExpression right = this.getRhs();
        SymbolicExpression rightEval = right.eval(vars);
        if (leftEval.isConstant() && rightEval.isConstant()) {
            return new Constant(leftEval.getValue() + rightEval.getValue());
        } else {
            return new Addition(leftEval, rightEval);
        }
    }
}