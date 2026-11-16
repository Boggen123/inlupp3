import org.ioopm.calculator.ast.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
 
/**
 * Tester för AST-delen (steg B1-B8 samt C1-C2). Kräver inte parsern.
 *
 * Skriver bara ut FEL, plus en rubrik per avsnitt och en summering.
 *
 * ANPASSA vid behov (allt samlat längst ned i filen, under "Anpassningspunkter"):
 *  - namedConstant(): hur din NamedConstant skapas.
 */
public class Test {
 
    private static int passed = 0;
    private static int failed = 0;
 
    private interface Action {
        void run() throws Exception;
    }
 
    public static void main(String[] args) throws Exception {
        section("B1/B2: konstruktion");           constructionTests();
        section("B3: enkla metoder");             methodTests();
        section("B5: utskrift");                  printingTests();
        section("B6: equals");                    equalityTests();
        section("B7: eval utan variabler");       evaluationTests();
        section("B7: Environment och variabler"); environmentTests();
        section("B7: tilldelning");               assignmentTests();
        section("Exempel ur uppgiften (4.1)");    specTableTests();
        section("B8: kommandon");                 commandTests();
        section("C1: namngivna konstanter");      namedConstantTests();
 
        System.out.println();
        System.out.println("Passed: " + passed + ", Failed: " + failed);
        if (failed > 0) {
            System.exit(1);
        }
    }
 
    // ------------------------------------------------------------------
    // B1/B2
    // ------------------------------------------------------------------
    private static void constructionTests() {
        SymbolicExpression x = new Variable("x");
        SymbolicExpression c = new Constant(3);
        SymbolicExpression[] all = {
            new Constant(0), new Variable("x"),
            new Addition(c, x), new Subtraction(c, x), new Multiplication(c, x),
            new Division(c, x), new Assignment(c, x),
            new Negation(x), new Sin(x), new Cos(x), new Exp(x), new Log(x)
        };
        check("alla konkreta klasser kan skapas", all.length == 12);
 
        // Kantfall: djupt träd
        SymbolicExpression deep = new Constant(0);
        for (int i = 0; i < 1000; i++) {
            deep = new Addition(deep, new Constant(1));
        }
        check("djupt träd (1000 nivåer) kan byggas", deep != null);
    }
 
    // ------------------------------------------------------------------
    // B3
    // ------------------------------------------------------------------
    private static void methodTests() {
        SymbolicExpression c = new Constant(5);
        SymbolicExpression x = new Variable("x");
 
        // isConstant
        check("Constant.isConstant", c.isConstant());
        check("Variable.isConstant är false", !x.isConstant());
        check("Addition.isConstant är false", !new Addition(c, c).isConstant());
        check("Sin.isConstant är false", !new Sin(c).isConstant());
        check("Assignment.isConstant är false", !new Assignment(c, x).isConstant());
 
        // getValue
        check("Constant(5).getValue", c.getValue() == 5.0);
        check("Constant(-2.5).getValue", new Constant(-2.5).getValue() == -2.5);
        check("Constant(0).getValue", new Constant(0).getValue() == 0.0);
        checkThrows("Variable.getValue kastar", () -> x.getValue());
        checkThrows("Addition.getValue kastar", () -> new Addition(c, c).getValue());
 
        // getName
        checkText("Addition.getName", "+", new Addition(c, c).getName());
        checkText("Subtraction.getName", "-", new Subtraction(c, c).getName());
        checkText("Multiplication.getName", "*", new Multiplication(c, c).getName());
        checkText("Division.getName", "/", new Division(c, c).getName());
        checkText("Assignment.getName", "=", new Assignment(c, x).getName());
        checkText("Sin.getName", "sin", new Sin(c).getName());
        checkText("Cos.getName", "cos", new Cos(c).getName());
        checkText("Exp.getName", "exp", new Exp(c).getName());
        checkText("Log.getName", "log", new Log(c).getName());
        checkText("Negation.getName", "-", new Negation(c).getName());
        checkThrows("Constant.getName kastar", () -> c.getName());
        checkThrows("Variable.getName kastar", () -> x.getName());
 
        // getPriority: bara ORDNINGEN testas, inte själva talen
        int atom = c.getPriority();
        int mul = new Multiplication(c, c).getPriority();
        int div = new Division(c, c).getPriority();
        int add = new Addition(c, c).getPriority();
        int sub = new Subtraction(c, c).getPriority();
        int asg = new Assignment(c, x).getPriority();
        check("atomer har högre prioritet än +", atom > add);
        check("Constant och Variable har samma prioritet", atom == x.getPriority());
        check("* har högre prioritet än +", mul > add);
        check("* och / har samma prioritet", mul == div);
        check("+ och - har samma prioritet", add == sub);
        check("tilldelning har lägst prioritet", asg < add && asg < mul);
    }
 
    // ------------------------------------------------------------------
    // B5
    // ------------------------------------------------------------------
    private static void printingTests() {
        SymbolicExpression a = new Variable("a");
        SymbolicExpression b = new Variable("b");
        SymbolicExpression c = new Variable("c");
        SymbolicExpression d = new Variable("d");
        SymbolicExpression x = new Variable("x");
        SymbolicExpression y = new Variable("y");
        SymbolicExpression z = new Variable("z");
 
        // Atomer
        p("42.0", new Constant(42));
        p("0.0", new Constant(0));
        p("-3.0", new Constant(-3));
        p("3.14", new Constant(3.14));
        p("x", x);
 
        // Enkla binära
        p("5.0 + 37.0", new Addition(new Constant(5), new Constant(37)));
        p("a - b", new Subtraction(a, b));
        p("a * b", new Multiplication(a, b));
        p("a / b", new Division(a, b));
 
        // Parenteser som behövs
        p("(5.0 + x) * 2.0",
            new Multiplication(new Addition(new Constant(5), x), new Constant(2)));
        p("(a + b) * (c - d)",
            new Multiplication(new Addition(a, b), new Subtraction(c, d)));
        p("a * (b + c) * d",
            new Multiplication(new Multiplication(a, new Addition(b, c)), d));
        p("2.0 * (3.0 + x)",
            new Multiplication(new Constant(2), new Addition(new Constant(3), x)));
 
        // Parenteser som inte behövs
        p("a * b * c", new Multiplication(new Multiplication(a, b), c));
        p("a + b * c", new Addition(a, new Multiplication(b, c)));
        p("a * b + c", new Addition(new Multiplication(a, b), c));
        p("a + b + c", new Addition(new Addition(a, b), c));
        p("a - b + c", new Addition(new Subtraction(a, b), c));
        p("a * b / c", new Division(new Multiplication(a, b), c));
 
        // Lika prioritet: höger sida behöver parentes, vänster inte
        p("a - b - c", new Subtraction(new Subtraction(a, b), c));
        p("a - (b - c)", new Subtraction(a, new Subtraction(b, c)));
        p("a / b / c", new Division(new Division(a, b), c));
        p("a / (b / c)", new Division(a, new Division(b, c)));
        p("a / (b * c)", new Division(a, new Multiplication(b, c)));
        p("a + (b - c)", new Addition(a, new Subtraction(b, c)));
 
        // Tilldelning
        p("3.0 = x", new Assignment(new Constant(3), x));
        p("2.0 = x = y", new Assignment(new Assignment(new Constant(2), x), y));
        p("(x + y = z) - 1.0",
            new Subtraction(new Assignment(new Addition(x, y), z), new Constant(1)));
        p("a * (1.0 = x)", new Multiplication(a, new Assignment(new Constant(1), x)));
 
        // Unära
        p("sin x", new Sin(x));
        p("cos x", new Cos(x));
        p("exp x", new Exp(x));
        p("log x", new Log(x));
        p("sin (a + b)", new Sin(new Addition(a, b)));
        p("sin (a * b)", new Sin(new Multiplication(a, b)));
        p("sin (3.0 = x)", new Sin(new Assignment(new Constant(3), x)));
        p("sin a + b", new Addition(new Sin(a), b));
        p("a + sin b", new Addition(a, new Sin(b)));
        p("sin cos a", new Sin(new Cos(a)));
 
        // Negation (förutsätter formatet "- a"; ändra om ni valt annat)
        p("- a", new Negation(a));
        p("- (a + b)", new Negation(new Addition(a, b)));
        p("- - a", new Negation(new Negation(a)));
    }
 
    // ------------------------------------------------------------------
    // B6
    // ------------------------------------------------------------------
    private static void equalityTests() {
        SymbolicExpression a = new Variable("a");
        SymbolicExpression b = new Variable("b");
 
        eq(true,  new Constant(5), new Constant(5));
        eq(false, new Constant(5), new Constant(6));
        eq(true,  new Constant(0), new Constant(0));
        eq(true,  new Variable("x"), new Variable("x"));
        eq(false, new Variable("x"), new Variable("y"));
        eq(false, new Variable("x"), new Variable("X"));       // skiftlägeskänsligt
        eq(false, new Constant(5), new Variable("x"));         // olika klasser
 
        eq(true,  new Addition(a, b), new Addition(a, b));
        eq(false, new Addition(a, b), new Addition(a, a));
        eq(false, new Addition(a, b), new Addition(b, a));     // ordningen spelar roll
        eq(false, new Addition(a, b), new Subtraction(a, b));  // samma barn, annan operator
        eq(false, new Multiplication(a, b), new Division(a, b));
        eq(false, new Addition(a, b), new Assignment(a, b));
 
        eq(true,  new Sin(a), new Sin(a));
        eq(false, new Sin(a), new Sin(b));
        eq(false, new Sin(a), new Cos(a));                     // samma barn, annan funktion
        eq(false, new Negation(a), new Subtraction(a, b));     // båda heter "-"
        eq(false, new Negation(a), new Sin(a));
 
        eq(true,
            new Multiplication(new Addition(a, b), new Constant(2)),
            new Multiplication(new Addition(a, b), new Constant(2)));
        eq(false,
            new Multiplication(new Addition(a, b), new Constant(2)),
            new Multiplication(new Addition(a, b), new Constant(3)));
        eq(false,
            new Multiplication(new Addition(a, b), new Constant(2)),
            new Multiplication(new Subtraction(a, b), new Constant(2)));
 
        // Reflexivitet och icke-objekt
        SymbolicExpression e = new Addition(a, new Sin(b));
        check("ett objekt är lika med sig själv", e.equals(e));
        Object nothing = null;
        check("equals(null) är false och kraschar inte", !e.equals(nothing));
        check("equals(String) är false", !e.equals("a + sin b"));
        check("Constant.equals(String) är false", !new Constant(5).equals("5.0"));
    }
 
    // ------------------------------------------------------------------
    // B7: eval utan variabler
    // ------------------------------------------------------------------
    private static void evaluationTests() throws Exception {
        SymbolicExpression x = new Variable("x");
 
        // Atomer
        evalIs(new Constant(5), new Constant(5));
        evalIs(new Variable("x"), new Variable("x"));
 
        // Grundläggande räkning
        evalIs(new Constant(42), new Addition(new Constant(5), new Constant(37)));
        evalIs(new Constant(-3), new Subtraction(new Constant(5), new Constant(8)));
        evalIs(new Constant(12), new Multiplication(new Constant(3), new Constant(4)));
        evalIs(new Constant(0.25), new Division(new Constant(1), new Constant(4)));
        evalIs(new Constant(6),
            new Multiplication(new Constant(2),
                new Subtraction(new Constant(5), new Constant(2))));
        evalIs(new Constant(21),
            new Multiplication(
                new Addition(new Constant(1), new Constant(2)),
                new Addition(new Constant(3), new Constant(4))));
        evalIs(new Constant(0.1),
            new Division(new Constant(1), new Addition(new Constant(2), new Constant(8))));
 
        // Funktioner
        evalIs(new Constant(0), new Sin(new Constant(0)));
        evalIs(new Constant(1), new Cos(new Constant(0)));
        evalIs(new Constant(1), new Exp(new Constant(0)));
        evalIs(new Constant(0), new Log(new Constant(1)));
        evalIs(new Constant(-5), new Negation(new Constant(5)));
        evalIs(new Constant(5), new Negation(new Negation(new Constant(5))));
        evalIs(new Constant(Math.sin(5)), new Sin(new Addition(new Constant(2), new Constant(3))));
 
        // Kantfall: division med noll och definitionsmängder
        evalIs(new Constant(Double.POSITIVE_INFINITY),
            new Division(new Constant(1), new Constant(0)));
        evalIs(new Constant(Double.NEGATIVE_INFINITY),
            new Division(new Constant(-1), new Constant(0)));
        evalIs(new Constant(Double.NEGATIVE_INFINITY), new Log(new Constant(0)));
        evalIs(new Constant(Double.POSITIVE_INFINITY), new Exp(new Constant(1000)));
        evalIsNaN("0/0 är NaN", new Division(new Constant(0), new Constant(0)));
        evalIsNaN("log(-1) är NaN", new Log(new Constant(-1)));
 
        // Delvis reducerat (okända variabler)
        evalIs(new Addition(new Constant(5), x), new Addition(new Constant(5), x));
        evalIs(new Multiplication(new Constant(5), x),
            new Multiplication(new Addition(new Constant(2), new Constant(3)), x));
        evalIs(new Addition(x, new Constant(5)),
            new Addition(x, new Addition(new Constant(2), new Constant(3))));
        evalIs(new Addition(new Constant(3), x),
            new Addition(new Addition(new Constant(1), new Constant(2)), x));
        evalIs(new Sin(x), new Sin(x));
        evalIs(new Sin(new Addition(x, new Constant(1))),
            new Sin(new Addition(x, new Constant(1))));
        evalIs(new Negation(x), new Negation(x));
 
        // eval får inte ändra det ursprungliga trädet
        SymbolicExpression original = new Addition(new Constant(5), new Constant(37));
        String before = original.toString();
        original.eval(new Environment());
        checkText("eval ändrar inte originalträdet", before, original.toString());
 
        // Idempotens: att utvärdera ett färdigt resultat igen ger samma resultat
        SymbolicExpression partial = new Addition(new Multiplication(new Constant(2), new Constant(3)), x);
        SymbolicExpression once = partial.eval(new Environment());
        check("eval är idempotent", once.eval(new Environment()).equals(once));
 
        // Djupt träd
        SymbolicExpression deep = new Constant(0);
        for (int i = 0; i < 1000; i++) {
            deep = new Addition(deep, new Constant(1));
        }
        evalIs(new Constant(1000), deep);
    }
 
    // ------------------------------------------------------------------
    // B7: Environment
    // ------------------------------------------------------------------
    private static void environmentTests() throws Exception {
        SymbolicExpression x = new Variable("x");
        SymbolicExpression y = new Variable("y");
 
        Environment env = new Environment();
        env.put(new Variable("x"), new Constant(8));
 
        evalIs(new Constant(8), x, env);
        evalIs(new Variable("y"), y, env);   // okänd variabel utvärderas till sig själv
        evalIs(new Constant(26),
            new Multiplication(new Addition(new Constant(5), x), new Constant(2)), env);
        evalIs(new Addition(new Constant(8), new Variable("y")),
            new Addition(x, y), env);
        evalIs(new Constant(Math.sin(8)), new Sin(x), env);
 
        // Nyckeluppslagning med ett nytt, lika objekt (kräver hashCode + equals)
        check("HashMap hittar variabel via lika nyckel",
            new Constant(8).equals(env.get(new Variable("x"))));
 
        // Utvärdering utan tilldelning ändrar inte miljön
        int sizeBefore = env.size();
        new Addition(x, y).eval(env);
        check("eval utan tilldelning ändrar inte miljön", env.size() == sizeBefore);
 
        // Två miljöer är oberoende
        Environment other = new Environment();
        evalIs(new Variable("x"), x, other);
        evalIs(new Constant(8), x, env);
    }
 
    // ------------------------------------------------------------------
    // B7: Assignment
    // ------------------------------------------------------------------
    private static void assignmentTests() throws Exception {
        SymbolicExpression x = new Variable("x");
        SymbolicExpression y = new Variable("y");
        SymbolicExpression z = new Variable("z");
 
        // 3 = x returnerar 3 och sparar x
        Environment env = new Environment();
        evalIs(new Constant(3), new Assignment(new Constant(3), x), env);
        evalIs(new Constant(3), x, env);
 
        // Omtilldelning skriver över
        evalIs(new Constant(9), new Assignment(new Constant(9), x), env);
        evalIs(new Constant(9), x, env);
 
        // Högersidan utvärderas inte: x har värde 9, men ska ändå få nytt värde
        evalIs(new Constant(7), new Assignment(new Constant(7), x), env);
        evalIs(new Constant(7), x, env);
 
        // Kedja: 2 = x = y
        Environment chain = new Environment();
        evalIs(new Constant(2),
            new Assignment(new Assignment(new Constant(2), x), y), chain);
        evalIs(new Constant(2), x, chain);
        evalIs(new Constant(2), y, chain);
 
        // Uttryck sparas oreducerat om det inte kan reduceras: z + 2 = y
        Environment expr = new Environment();
        evalIs(new Addition(z, new Constant(2)),
            new Assignment(new Addition(z, new Constant(2)), y), expr);
        evalIs(new Addition(z, new Constant(2)), y, expr);
 
        // Självtilldelning ska inte ge oändlig rekursion
        Environment self = new Environment();
        evalIs(new Variable("x"), new Assignment(x, x), self);
        evalIs(new Variable("x"), x, self);
 
        // Utvärdering från vänster till höger: x + (1 = x) med x = 2 ska bli 3
        Environment order = new Environment();
        order.put(new Variable("x"), new Constant(2));
        evalIs(new Constant(3),
            new Addition(x, new Assignment(new Constant(1), x)), order);
        evalIs(new Constant(1), x, order);   // och x är nu 1
 
        // Tilldelning inuti uttryck, exemplet ur uppgiften:
        // (x+y=z) - (z-1)*(1=x) efter 2 = x = y ska bli 1.0
        Environment spec = new Environment();
        new Assignment(new Assignment(new Constant(2), x), y).eval(spec);
        evalIs(new Constant(1),
            new Subtraction(
                new Assignment(new Addition(x, y), z),
                new Multiplication(
                    new Subtraction(z, new Constant(1)),
                    new Assignment(new Constant(1), x))),
            spec);
 
        // Försvar: högersidan är inte en variabel (parsern ska aldrig skapa detta)
        checkThrows("tilldelning till icke-variabel kastar",
            () -> new Assignment(new Constant(1), new Constant(2)).eval(new Environment()));
    }
 
    // ------------------------------------------------------------------
    // Tabellen i avsnitt 4.1 i uppgiften, steg för steg i EN miljö
    // ------------------------------------------------------------------
    private static void specTableTests() throws Exception {
        Variable x = new Variable("x");
        Variable y = new Variable("y");
        Variable z = new Variable("z");
        Variable f = new Variable("f");
        Variable g = new Variable("g");
        Environment env = new Environment();
 
        // 3=x -> 3
        evalIs(new Constant(3), new Assignment(new Constant(3), x), env);
        // x+y -> 3+y
        evalIs(new Addition(new Constant(3), y), new Addition(x, y), env);
        // z+2=y -> z+2
        evalIs(new Addition(z, new Constant(2)),
            new Assignment(new Addition(z, new Constant(2)), y), env);
        // 3=z -> 3
        evalIs(new Constant(3), new Assignment(new Constant(3), z), env);
        // y -> z+2 (symboler utvärderas bara en gång, annars hade det blivit 5)
        evalIs(new Addition(z, new Constant(2)), y, env);
        // y=f -> z+2
        evalIs(new Addition(z, new Constant(2)), new Assignment(y, f), env);
        // f+z*2 -> z+2+6
        evalIs(new Addition(new Addition(z, new Constant(2)), new Constant(6)),
            new Addition(f, new Multiplication(z, new Constant(2))), env);
        // z+x=g -> 6
        evalIs(new Constant(6), new Assignment(new Addition(z, x), g), env);
        // g -> 6, f -> z+2, z -> 3
        evalIs(new Constant(6), g, env);
        evalIs(new Addition(z, new Constant(2)), f, env);
        evalIs(new Constant(3), z, env);
        // (f+z*z) -> z+2+9
        evalIs(new Addition(new Addition(z, new Constant(2)), new Constant(9)),
            new Addition(f, new Multiplication(z, z)), env);
 
        // Det andra exemplet: tilldelning ändrar inte tidigare uttryck
        Variable a = new Variable("a");
        Variable b = new Variable("b");
        Variable c = new Variable("c");
        Environment env2 = new Environment();
        evalIs(new Variable("b"), b, env2);
        evalIs(new Constant(1), new Assignment(new Constant(1), a), env2);
        evalIs(new Addition(new Constant(1), b), new Addition(a, b), env2);
        evalIs(new Addition(b, new Constant(1)),
            new Assignment(new Addition(b, a), c), env2);
        evalIs(new Constant(2), new Assignment(new Constant(2), b), env2);
        evalIs(new Addition(b, new Constant(1)), c, env2);   // c ändras inte
    }
 
    // ------------------------------------------------------------------
    // B8
    // ------------------------------------------------------------------
    private static void commandTests() {
        check("Vars är en singleton", Vars.instance() == Vars.instance());
        check("Quit är en singleton", Quit.instance() == Quit.instance());
        check("Clear är en singleton", Clear.instance() == Clear.instance());
        check("kommandona är olika objekt",
            (Object) Vars.instance() != (Object) Quit.instance()
            && (Object) Quit.instance() != (Object) Clear.instance()
            && (Object) Vars.instance() != (Object) Clear.instance());
 
        check("Vars.isCommand", Vars.instance().isCommand());
        check("Quit.isCommand", Quit.instance().isCommand());
        check("Clear.isCommand", Clear.instance().isCommand());
 
        SymbolicExpression x = new Variable("x");
        check("Constant.isCommand är false", !new Constant(1).isCommand());
        check("Variable.isCommand är false", !x.isCommand());
        check("Addition.isCommand är false", !new Addition(x, x).isCommand());
        check("Assignment.isCommand är false", !new Assignment(x, x).isCommand());
        check("Sin.isCommand är false", !new Sin(x).isCommand());
 
        // Konstruktorerna ska vara privata (annars kan man skapa fler instanser)
        for (Class<?> cls : new Class<?>[] { Vars.class, Quit.class, Clear.class }) {
            for (Constructor<?> k : cls.getDeclaredConstructors()) {
                check(cls.getSimpleName() + ": konstruktorn är privat",
                    Modifier.isPrivate(k.getModifiers()));
            }
        }
 
        // Kommandon får inte utvärderas
        checkThrows("Vars.eval kastar", () -> Vars.instance().eval(new Environment()));
        checkThrows("Quit.eval kastar", () -> Quit.instance().eval(new Environment()));
        checkThrows("Clear.eval kastar", () -> Clear.instance().eval(new Environment()));
    }
 
    // ------------------------------------------------------------------
    // C1
    // ------------------------------------------------------------------
    private static void namedConstantTests() throws Exception {
        SymbolicExpression answer = namedConstant("Answer", 42);
        SymbolicExpression pi = namedConstant("pi", Math.PI);
 
        check("NamedConstant.isConstant", answer.isConstant());
        check("NamedConstant.getValue", answer.getValue() == 42.0);
        checkText("NamedConstant skriver ut sitt namn", "pi", pi.toString());
 
        // Räkning med namngivna konstanter
        SymbolicExpression sum = new Addition(answer, answer).eval(new Environment());
        check("Answer + Answer är 84", sum.isConstant() && sum.getValue() == 84.0);
 
        SymbolicExpression mixed = new Addition(answer, new Constant(1)).eval(new Environment());
        check("Answer + 1 är 43", mixed.isConstant() && mixed.getValue() == 43.0);
 
        SymbolicExpression withVar = new Addition(answer, new Variable("x")).eval(new Environment());
        check("Answer + x kan inte reduceras", !withVar.isConstant());
 
        // Får användas som VÄRDE i en tilldelning
        Environment env = new Environment();
        SymbolicExpression r = new Assignment(pi, new Variable("x")).eval(env);
        check("pi = x ger x värdet pi", r.isConstant() && r.getValue() == Math.PI);
 
        // Får INTE tilldelas
        final Environment guarded = new Environment();
        checkThrowsNamed("43 = Answer kastar IllegalAssignmentException",
            "IllegalAssignmentException",
            () -> new Assignment(new Constant(43), answer).eval(guarded));
        check("misslyckad tilldelning lämnar miljön orörd", guarded.isEmpty());
        checkThrowsNamed("x = pi kastar IllegalAssignmentException",
            "IllegalAssignmentException",
            () -> new Assignment(new Variable("x"), pi).eval(guarded));
    }
 
    // ==================================================================
    // Anpassningspunkter: ändra här om dina klasser ser annorlunda ut
    // ==================================================================
    private static SymbolicExpression namedConstant(String name, double value) {
        return new NamedConstant(name, value);
    }
 
    // ==================================================================
    // Hjälpmetoder
    // ==================================================================
    private static void section(String title) {
        System.out.println("--- " + title);
    }
 
    private static void fail(String message) {
        failed++;
        System.out.println("FAILED: " + message);
    }
 
    private static void check(String label, boolean condition) {
        if (condition) {
            passed++;
        } else {
            fail(label);
        }
    }
 
    private static void checkText(String label, String expected, String actual) {
        if (expected.equals(actual)) {
            passed++;
        } else {
            fail(label + ": expected '" + expected + "' but got '" + actual + "'");
        }
    }
 
    private static void checkThrows(String label, Action action) {
        try {
            action.run();
            fail(label + " (inget undantag kastades)");
        } catch (Exception e) {
            passed++;
        }
    }
 
    private static void checkThrowsNamed(String label, String simpleName, Action action) {
        try {
            action.run();
            fail(label + " (inget undantag kastades)");
        } catch (Exception e) {
            if (simpleName.equals(e.getClass().getSimpleName())) {
                passed++;
            } else {
                fail(label + " (fick " + e.getClass().getSimpleName() + ")");
            }
        }
    }
 
    // Utskrift
    private static void p(String expected, SymbolicExpression e) {
        checkText("print " + expected, expected, "" + e);
    }
 
    // equals, testar båda riktningarna (symmetri)
    private static void eq(boolean expected, SymbolicExpression a, SymbolicExpression b) {
        boolean ab = a.equals(b);
        boolean ba = b.equals(a);
        if (ab == expected && ba == expected) {
            passed++;
        } else {
            fail("equals(" + a + ", " + b + "): expected " + expected
                + " but got " + ab + " / " + ba);
        }
    }
 
    // eval med tom ny miljö
    private static void evalIs(SymbolicExpression expected, SymbolicExpression e) throws Exception {
        evalIs(expected, e, new Environment());
    }
 
    private static void evalIs(SymbolicExpression expected, SymbolicExpression e,
                               Environment env) throws Exception {
        String before = e.toString();
        SymbolicExpression r = e.eval(env);
        if (r.equals(expected)) {
            passed++;
        } else {
            fail("eval(" + before + "): expected '" + expected + "' but got '" + r + "'");
        }
    }
 
    // NaN == NaN är false i Java, så equals() kan inte användas
    private static void evalIsNaN(String label, SymbolicExpression e) throws Exception {
        SymbolicExpression r = e.eval(new Environment());
        check(label, r.isConstant() && Double.isNaN(r.getValue()));
    }
}
 