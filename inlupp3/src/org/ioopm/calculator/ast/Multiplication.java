package org.ioopm.calculator.ast;

public class Multiplication extends Binary {
    public Multiplication(SymbolicExpression v1, SymbolicExpression v2){
        super(v1, v2);
    }

    public String getName(){
        return "*";
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
            return new Constant(leftEval.getValue() * rightEval.getValue());
        } else {
            return new Multiplication(leftEval, rightEval);
        }
    }
}
