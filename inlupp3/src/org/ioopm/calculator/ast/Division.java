package org.ioopm.calculator.ast;

public class Division extends Binary {
    public Division(SymbolicExpression t, SymbolicExpression n) {
    super(t, n);
    }

    public String getName(){
        return "/";
    }

    public int getPriority(){
        return 100;
    }

        public SymbolicExpression eval(Environment vars) {
        SymbolicExpression left = this.getLhs();
        SymbolicExpression leftEval = left.eval(vars);
        SymbolicExpression right = this.getRhs();
        SymbolicExpression rightEval = right.eval(vars);
        if (leftEval.isConstant() && rightEval.isConstant()) {
            return new Constant(leftEval.getValue() / rightEval.getValue());
        } else {
            return new Division(leftEval, rightEval);
        }
    }
}
