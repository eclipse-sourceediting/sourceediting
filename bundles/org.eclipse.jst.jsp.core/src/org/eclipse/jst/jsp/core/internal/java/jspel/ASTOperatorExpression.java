package org.eclipse.jst.jsp.core.internal.java.jspel;

import java.util.ArrayList;
import java.util.List;

public class ASTOperatorExpression extends SimpleNode {
	protected ArrayList operatorTokens = new ArrayList();

	public ASTOperatorExpression(int i) {
		super(i);
	}
	
	public ASTOperatorExpression(JSPELParser p, int i) {
		this(i);
		parser = p;
	}

	public void addOperatorToken(Token opToken) {
		operatorTokens.add(opToken);
	}
	
	public List getOperatorTokens() {
		return operatorTokens;
	}
}
