package org.ioopm.calculator.ast;

public abstract class SymbolicExpression {
    public boolean isConstant(){
        return false;
    }
    
    public boolean isCommand(){
    	return false;
    }
    
    public String getName(){
        throw new SyntaxErrorException("getName() called on expression with no operator");
    }

    public  int getPriority(){
        return 200;
    }

    public  double getValue(){
        throw new IllegalAssignmentException("getValue( called on non-constant");
    }

        public SymbolicExpression eval(Environment vars) {
        return this;
    }

    public abstract String toString();
}
