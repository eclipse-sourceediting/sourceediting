/*******************************************************************************
 * Copyright (c) 2005 BEA Systems and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     BEA Systems - initial implementation
 *     
 *******************************************************************************/
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
