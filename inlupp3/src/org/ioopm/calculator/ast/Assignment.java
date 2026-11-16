package org.ioopm.calculator.ast;

import javax.management.RuntimeErrorException;

import org.ioopm.calculator.ast.Variable;

public class Assignment extends Binary {
        public Assignment(SymbolicExpression l, SymbolicExpression r){
        super(l, r);
    }

    public String getName(){
        return "=";
    }

    public int getPriority(){
        return 10;
    }

     public SymbolicExpression eval(Environment vars) {
        SymbolicExpression value = this.getLhs().eval(vars);
        SymbolicExpression target = this.getRhs();

        if (target instanceof Variable) {
            if (Constants.namedConstants.containsKey(target.toString())) {
                throw new IllegalAssignmentException(
                    "Error: cannot redefine named constant '" + target + "'");
            }
            vars.put((Variable) target, value);
            return value;
        }

        if (target.isConstant()) {
            throw new IllegalAssignmentException(
                "Error: cannot redefine named constant '" + target + "'");
        }

        throw new IllegalAssignmentException("Right hand side is not a variable");
    }
}
