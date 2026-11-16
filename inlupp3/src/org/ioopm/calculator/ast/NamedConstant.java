package org.ioopm.calculator.ast;

public class NamedConstant extends Constants {
        private String key;
        private double value;

        public NamedConstant (String key, double value){
            this.key = key;
            this.value = value;
        }
    
        public SymbolicExpression eval() {
            return new Constant(this.value);
        }

        public double getValue(){
            return this.value;
        }
        public boolean isConstant(){
            return true;
        }
        public String toString(){
            return this.key;
        }
    }