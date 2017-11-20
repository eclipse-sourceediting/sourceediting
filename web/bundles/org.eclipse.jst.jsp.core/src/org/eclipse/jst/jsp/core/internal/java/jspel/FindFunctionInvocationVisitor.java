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

public class FindFunctionInvocationVisitor implements JSPELParserVisitor {
	protected int fCursorPosition;
	
	public FindFunctionInvocationVisitor(int cursorPosition)
	{
		fCursorPosition = cursorPosition;
	}
	
	protected boolean isEnclosing(SimpleNode node) {
		return(node.firstToken.beginColumn < fCursorPosition && node.lastToken.endColumn >= fCursorPosition);
	}

	
	protected Object visitEnclosingChild(SimpleNode node, Object data)	{
		if(null == node.children)
			return null;
		
		for(int i = 0; i < node.children.length; i++) {
			if(isEnclosing(node)) 
				return node.children[i].jjtAccept(this, data);
		}
		return null;
	}

	public Object visit(SimpleNode node, Object data) {
		return visitEnclosingChild(node, data);
	}

	public Object visit(ASTExpression node, Object data) {
		return visitEnclosingChild(node, data);
	}

	public Object visit(ASTOrExpression node, Object data) {
		return visitEnclosingChild(node, data);
	}

	public Object visit(ASTAndExpression node, Object data) {
		return visitEnclosingChild(node, data);
	}

	public Object visit(ASTEqualityExpression node, Object data) {
		return visitEnclosingChild(node, data);
	}

	public Object visit(ASTRelationalExpression node, Object data) {
		return visitEnclosingChild(node, data);
	}

	public Object visit(ASTAddExpression node, Object data) {
		return visitEnclosingChild(node, data);
	}

	public Object visit(ASTMultiplyExpression node, Object data) {
		return visitEnclosingChild(node, data);
	}

	public Object visit(ASTChoiceExpression node, Object data) {
		return visitEnclosingChild(node, data);
	}

	public Object visit(ASTUnaryExpression node, Object data) {
		return visitEnclosingChild(node, data);
	}

	public Object visit(ASTValue node, Object data) {
		return visitEnclosingChild(node, data);
	}

	public Object visit(ASTValuePrefix node, Object data) {
		return visitEnclosingChild(node, data);
	}

	public Object visit(ASTValueSuffix node, Object data) {
		return visitEnclosingChild(node, data);
	}

	public Object visit(ASTFunctionInvocation node, Object data) {
		Object nestedInvocation = visitEnclosingChild(node, data);
		if(null != nestedInvocation)
			return nestedInvocation;
		return node;
	}

	public Object visit(ASTLiteral node, Object data) {
		return visitEnclosingChild(node, data);
	}
}
