package org.ioopm.calculator.ast;
import java.util.HashMap;

public abstract class SymbolicExpression {
    public boolean isConstant(){
        return false;
    }
    public String getName(){
        throw new RuntimeException("getName() called on expression with no operator");
    }

    public  int getPriority(){
        return 200;
    }

    public  double getValue(){
        throw new RuntimeException("getValue( called on non-constnt");
    }

        public abstract SymbolicExpression eval() {
        throw new RuntimeException("eval() not implemented yet");
    }
}
