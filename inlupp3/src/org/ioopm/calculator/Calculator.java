package org.ioopm.calculator;

import org.ioopm.calculator.ast.*;
import org.ioopm.calculator.parser.*;
import java.io.IOException;

public class Calculator {
    public static void main(String[] args) {

        Environment env = new Environment();
        final CalculatorParser parser = new CalculatorParser();
        int expressions = 0;
        int successfulEvals = 0;
        int fullEvals = 0;
        boolean run = true;
        SymbolicExpression ans;

        System.out.println("Välkommen till kalkylatorn!");
        while(run) {
            String input = System.console().readLine();
            expressions ++;

            boolean failed = true;
            Environment backup = new Environment();
            backup.putAll(env);
            try {
                SymbolicExpression parsed = parser.parse(input, env);
                if(parsed.isCommand()) {
                    if(input.equals("Clear")) {
                        System.out.println("Variablerna är nu borttagna!");
                        env.clear();
                    } else if (input.equals("Quit")) {
                        System.out.println("Tack för du använde kalkylatorn, här kommer lite statistik:");
                        System.out.println("Expressions: "+ expressions + "\nSuccesful Evaluations: " + successfulEvals + "\nFull Evaluations: " + fullEvals);
                        run = false;
                    } else if (input.equals("Vars")) {
                        System.out.println("Sparade variabler:");
                        System.out.println(env);
                    }
                } else {
                    ans = parsed.eval(env);
                    successfulEvals++;
                    System.out.println(ans);
                    env.put(new Variable("ans"), ans);
                    if(ans.isConstant()){
                        fullEvals++;
                    }

                }
                failed=false;
            } catch(IOException e) {
                e.getMessage();
            } catch(SyntaxErrorException e) {
                e.getMessage();
            } catch(IllegalExpressionException e) {
                e.getMessage();
            } catch(IllegalAssignmentException e) {
                e.getMessage();
            }
            if(failed){
                env.clear();
                env.putAll(backup);
            }
        }

    }
}
