/*******************************************************************************
 * Copyright (c) 2008 Standards for Technology in Automotive Retail and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Carver (STAR) - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xml.xpath.core.internal.parser;


import java.io.StringReader;

import org.apache.commons.jxpath.ri.parser.Token;

public class XPathParser {
	
	protected org.apache.commons.jxpath.ri.parser.XPathParser parser = null;
	protected Token currentToken = null;
	protected Token previousToken = null;
	
	public XPathParser(String xpath) {
		parser = new org.apache.commons.jxpath.ri.parser.XPathParser(new StringReader(xpath));
	}
	
	/**
	 * Given a line number and a column number, return the starting
	 * offset of the last known token.
	 * @param offsetLine
	 * @param offsetColumn
	 * @return
	 */
	public int getTokenStartOffset(int offsetLine, int offsetColumn) {
		currentToken = parser.getNextToken();
		previousToken = currentToken;
		
		while (currentToken != null) {
			if (locatedLine(currentToken, offsetLine)) {
				if (locatedColumn(currentToken, offsetColumn)) {
					if (previousToken.kind == 78) {
						return previousToken.beginColumn;
					}
					if (currentToken.kind == 80 || currentToken.kind == 44) {
						return currentToken.endColumn + 1;
					}
					return currentToken.beginColumn;
				} else if (currentToken.beginColumn > offsetColumn && previousToken.beginColumn > offsetColumn) {
					return offsetColumn;
				}
			}
			
			if (currentToken.beginColumn == currentToken.next.beginColumn &&
					currentToken.beginLine == currentToken.next.beginLine) {
				if (currentToken.beginColumn == 0) {
					return 1;
				} else {
					if (currentToken.beginColumn == currentToken.endColumn) {
						return currentToken.endColumn + 1;
					}
					return currentToken.beginColumn;
				}
			}
			

			previousToken = currentToken;
			currentToken = parser.getNextToken();
		}
		return previousToken.beginColumn;
	}	
	
	/**
	 * Checks to see if the token is in the range of the line offset
	 * @param token An XPath Token.
	 * @param offsetLine Line number offset of a region.
	 * @return true if found, false otherwise.
	 */
	protected boolean locatedLine(Token token, int offsetLine) {
		return token.beginLine <= offsetLine &&
		       token.endLine >= offsetLine;
	}
	
	/**
	 * Checks to see if the token is in the range of the column offset
	 * @param token
	 * @param offsetColumn
	 * @return true if found, false otherwise
	 */
	protected boolean locatedColumn(Token token, int offsetColumn) {
		return token.beginColumn <= offsetColumn &&
		       token.endColumn >= offsetColumn;
	}
	
	public Token getCurrentToken() {
		return currentToken;
	}
	
	public Token getPreviousToken() {
		return previousToken;
	}
	
	
	
	

}
