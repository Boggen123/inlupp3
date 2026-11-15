package org.ioopm.calculator.ast;

import org.ioopm.calculator.ast.Environment;
import org.ioopm.calculator.ast.SymbolicExpression;

public class Clear extends Command {
	private static final Clear theInstance = new Clear();
	private Clear() {}
	public static Clear instance() {
		return theInstance;
	}

	public SymbolicExpression eval(Environment vars) {
		vars.clear();
		return this;
	}

	public String toString() {
		return "clear";
	}
	
}
