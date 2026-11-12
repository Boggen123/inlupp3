import org.ioopm.calculator.ast.*;
//AI generated test code for the AST classes. You can use it to check if your implementation is correct.
public class Test {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        constructionTests();
        printingTests();
        // evaluationTests();   // avkommentera i B7 (kräver eval(), equals(), Environment)

        System.out.println();
        System.out.println("Passed: " + passed + ", Failed: " + failed);
    }

    // ------------------------------------------------------------------
    // B2: kan alla konkreta klasser skapas? (Ska fungera redan nu.)
    // ------------------------------------------------------------------
    private static void constructionTests() {
        SymbolicExpression x = new Variable("x");
        SymbolicExpression c = new Constant(3);

        new Addition(c, x);
        new Subtraction(c, x);
        new Multiplication(c, x);
        new Division(c, x);
        new Assignment(c, x);
        new Negation(x);
        new Sin(x);
        new Cos(x);
        new Exp(x);
        new Log(x);
        // Vars, Quit, Clear läggs till här efter B8 (via instance())

        System.out.println("Construction OK");
    }

    // ------------------------------------------------------------------
    // B4/B5: utskrift. Förväntad text är uträknad för hand.
    // De ska FELA tills toString() är skriven.
    // Antaganden: tal skrivs som 5.0, binära som "vänster op höger",
    // unära som "namn argument". Ändra om ni bestämt annat format.
    // ------------------------------------------------------------------
    private static void printingTests() {
        SymbolicExpression a = new Variable("a");
        SymbolicExpression b = new Variable("b");
        SymbolicExpression c = new Variable("c");
        SymbolicExpression d = new Variable("d");

        // Atomer
        testPrinting("42.0", new Constant(42));
        testPrinting("x", new Variable("x"));

        // Enkla binära
        testPrinting("5.0 + 37.0", new Addition(new Constant(5), new Constant(37)));
        testPrinting("a - b", new Subtraction(a, b));
        testPrinting("a * b", new Multiplication(a, b));
        testPrinting("a / b", new Division(a, b));

        // Parenteser som BEHÖVS
        testPrinting("(5.0 + x) * 2.0",
            new Multiplication(new Addition(new Constant(5), new Variable("x")),
                               new Constant(2)));
        testPrinting("(a + b) * (c - d)",
            new Multiplication(new Addition(a, b), new Subtraction(c, d)));

        // Parenteser som INTE behövs
        testPrinting("a * b * c",
            new Multiplication(new Multiplication(a, b), c));
        testPrinting("a + b * c",
            new Addition(a, new Multiplication(b, c)));

        // Vänster- kontra högerassociativitet: a - (b - c) != a - b - c
        testPrinting("a - (b - c)",
            new Subtraction(a, new Subtraction(b, c)));
        testPrinting("a - b - c",
            new Subtraction(new Subtraction(a, b), c));

        // Tilldelning: värde till vänster, variabel till höger
        testPrinting("3.0 = x", new Assignment(new Constant(3), new Variable("x")));

        // Unära
        testPrinting("sin x", new Sin(new Variable("x")));
        testPrinting("cos x", new Cos(new Variable("x")));
        testPrinting("exp x", new Exp(new Variable("x")));
        testPrinting("log x", new Log(new Variable("x")));

        // Lägg till egna: negation, nästlade unära, tilldelning inuti uttryck ...
    }

    // ------------------------------------------------------------------
    // B6/B7: evaluering. Avkommentera hela blocket när eval() finns.
    // Skapa en NY Environment per test så att inget läcker mellan dem.
    // ------------------------------------------------------------------
    /*
    private static void evaluationTests() {
        // Utan variabler
        testEvaluating(new Constant(42),
            new Addition(new Constant(5), new Constant(37)));
        testEvaluating(new Constant(6),
            new Multiplication(new Constant(2),
                new Subtraction(new Constant(5), new Constant(2))));
        testEvaluating(new Constant(0), new Sin(new Constant(0)));

        // Kan inte reduceras: x saknar värde
        testEvaluating(new Addition(new Constant(5), new Variable("x")),
            new Addition(new Constant(5), new Variable("x")));

        // Med variabel i miljön: (5 + x) * 2 med x = 8 ska bli 26
        Environment env1 = new Environment();
        env1.put(new Variable("x"), new Constant(8));
        testEvaluating(new Constant(26),
            new Multiplication(new Addition(new Constant(5), new Variable("x")),
                               new Constant(2)),
            env1);

        // Delvis reducerat: x = 1, y okänd
        Environment env2 = new Environment();
        env2.put(new Variable("x"), new Constant(1));
        testEvaluating(new Addition(new Constant(1), new Variable("y")),
            new Addition(new Variable("x"), new Variable("y")),
            env2);

        // Tilldelning returnerar värdet och sparar det
        Environment env3 = new Environment();
        testEvaluating(new Constant(3),
            new Assignment(new Constant(3), new Variable("x")),
            env3);
        testEvaluating(new Constant(3), new Variable("x"), env3);

        // Division med noll lämnas åt Java: Infinity
        testEvaluating(new Constant(Double.POSITIVE_INFINITY),
            new Division(new Constant(1), new Constant(0)));
    }

    private static void testEvaluating(SymbolicExpression expected, SymbolicExpression e) {
        testEvaluating(expected, e, new Environment());
    }

    private static void testEvaluating(SymbolicExpression expected, SymbolicExpression e,
                                       Environment env) {
        SymbolicExpression r = e.eval(env);
        if (r.equals(expected)) {
            passed++;
            System.out.println("Passed: " + e + "  ==>  " + r);
        } else {
            failed++;
            System.out.println("Error: expected '" + expected + "' but got '" + r + "'");
        }
    }
    */

    // ------------------------------------------------------------------
    // Hjälpmetod för utskriftstester
    // ------------------------------------------------------------------
    private static void testPrinting(String expected, SymbolicExpression e) {
        if (expected.equals("" + e)) {
            passed++;
            System.out.println("Passed: " + e);
        } else {
            failed++;
            System.out.println("Error: expected '" + expected + "' but got '" + e + "'");
        }
    }
}