import org.ioopm.calculator.parser.CalculatorParser;
import org.ioopm.calculator.ast.*;

import java.io.IOException;

public class TestParser {

    public static void main(String[] args) throws IOException {
        SymbolicExpression e = new CalculatorParser().parse("1 + 2", new Environment());
        System.out.println(e.toString().equals("1 + 2") ? "Pass" : "Fail: got " + e);
    }

}