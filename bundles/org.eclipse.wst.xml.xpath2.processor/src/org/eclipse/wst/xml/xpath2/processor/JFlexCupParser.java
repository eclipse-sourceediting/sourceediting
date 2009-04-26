/*******************************************************************************
 * Copyright (c) 2005, 2009 Andrea Bittau, University College London, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andrea Bittau - initial API and implementation from the PsychoPath XPath 2.0 
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor;

import java_cup.runtime.*;
import java.io.*;

import org.eclipse.wst.xml.xpath2.processor.ast.*;

/**
 * JFlexCupParser parses the xpath expression
 */
public class JFlexCupParser implements XPathParser {

	/**
	 * Tries to parse the xpath expression
	 * 
	 * @param xpath
	 *            is the xpath string.
	 * @throws XPathParserException.
	 * @return the xpath value.
	 */
	public XPath parse(String xpath) throws XPathParserException {

		XPathFlex lexer = new XPathFlex(new StringReader(xpath));

		XpathCup p = new XpathCup(lexer);
		try {
			Symbol res = p.parse();
			return (XPath) res.value;

		} catch (JFlexError e) {
			throw new XPathParserException("JFlex lexer error: " + e.reason());
		} catch (CupError e) {
			throw new XPathParserException("CUP parser error: " + e.reason());
		} catch (Exception e) {
			String err = "Unknown error at line " + lexer.lineno();

			err += " col " + lexer.colno();
			err += ": " + lexer.yytext();

			throw new XPathParserException(err);
		}
	}
}
