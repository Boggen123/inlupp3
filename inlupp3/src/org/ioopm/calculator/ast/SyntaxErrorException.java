package org.ioopm.calculator.ast;

public class SyntaxErrorException extends RuntimeException {

   public SyntaxErrorException(String str){
        super(str);
   }

}