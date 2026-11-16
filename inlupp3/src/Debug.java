import org.ioopm.calculator.ast.*;

public class Debug {
    public static void main(String[] args) {
Environment env = new Environment();
Variable x = new Variable("x");
Variable y = new Variable("y");

System.out.println("1. " + new Assignment(new Constant(3), x).eval(env));
System.out.println("2. env: " + env);
System.out.println("3. x:  " + x.eval(env));
System.out.println("4. x+y: " + new Addition(x, y).eval(env));
    }
}