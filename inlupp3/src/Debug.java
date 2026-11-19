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
System.out.println(new Sin(new Constant(0)).eval(env));   // förväntat: 0.0
System.out.println(new Cos(new Constant(0)).eval(env));   // förväntat: 1.0
System.out.println(new Exp(new Constant(0)).eval(env));   // förväntat: 1.0
System.out.println(new Log(new Constant(1)).eval(env));   // förväntat: 0.0
System.out.println(new Sin(new Variable("x")).eval(env)); // förväntat: sin x
System.out.println(new Sin(new Variable("y")).eval(env)); // förväntat: sin x
    }
}