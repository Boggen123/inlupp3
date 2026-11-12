package org.ioopm.calculator.ast;

public abstract class Unary extends SymbolicExpression {
        private SymbolicExpression value;


    public Unary(SymbolicExpression value){
        this.value = value;
    }

    public int getPriority(){
        return 150;
    }

    public String toString(){
        String arg = this.value.toString();
        if (this.value.getPriority() < this.getPriority()){
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
            && this.value.equals(other.value);
    }
}
