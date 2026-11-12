package org.ioopm.calculator.ast;

public abstract class Unary extends SymbolicExpression {
        private SymbolicExpression arg;


    public Unary(SymbolicExpression arg){
        this.arg = arg;
    }

    public SymbolicExpression getArg(){
        return this.arg;
    }
    public int getPriority(){
        return 150;
    }

    public String toString(){
        String arg = this.arg.toString();
        if (this.arg.getPriority() < this.getPriority()){
            arg = "(" + arg + ")";
        }
        return this.getName() + " " + arg;
    }



    public boolean equals(Object other) {
        if (other instanceof Unary) {
            return this.equals((Unary) other);
        } else {
            return false;
        }
    }

    public boolean equals(Unary other) {
        return this.getName().equals(other.getName())
            && this.arg.equals(other.arg);
    }
}
