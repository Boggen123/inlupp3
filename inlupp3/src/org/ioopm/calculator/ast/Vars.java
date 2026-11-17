package org.ioopm.calculator.ast;

import org.ioopm.calculator.ast.Environment;
import org.ioopm.calculator.ast.SymbolicExpression;

public class Vars extends Command {
	private static final Vars theInstace = new Vars();
	private Vars() {}
	public static Vars instance() {
		return theInstace;
	}

	public SymbolicExpression eval(Environment vars) throws IllegalAssignmentException {
		throw new IllegalAssignmentException("Cannot eval command");
	}

	public String toString() {
		return "Vars";
	}
}
