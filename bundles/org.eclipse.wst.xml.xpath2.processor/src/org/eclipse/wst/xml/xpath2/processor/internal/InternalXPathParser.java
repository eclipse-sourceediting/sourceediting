/*******************************************************************************
 * Copyright (c) 2005, 2009 Andrea Bittau, University College London, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andrea Bittau - initial API and implementation from the PsychoPath XPath 2.0
 *     Bug 338494    - prohibiting xpath expressions starting with / or // to be parsed. 
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor.internal;

import java.io.StringReader;

import java_cup.runtime.Symbol;

import org.eclipse.wst.xml.xpath2.processor.StaticError;
import org.eclipse.wst.xml.xpath2.processor.XPathParserException;
import org.eclipse.wst.xml.xpath2.processor.ast.XPath;

/**
 * JFlexCupParser parses the xpath expression
 */
public class InternalXPathParser {

	/**
	 * Tries to parse the xpath expression
	 * 
	 * @param xpath
	 *            is the xpath string.
	 * @param isRootlessAccess
	 *            if 'true' then PsychoPath engine can't parse xpath expressions starting with / or //.
	 * @throws XPathParserException.
	 * @return the xpath value.
	 * @since 2.0
	 */
	public XPath parse(String xpath, boolean isRootlessAccess) throws XPathParserException {

		XPathFlex lexer = new XPathFlex(new StringReader(xpath));

		XPathCup p = null;
		if (isRootlessAccess) {
			p = new XPathCupRestricted(lexer); 
		}
		else {
			p = new XPathCup(lexer); 
		}
		try {
			Symbol res = p.parse();
			return (XPath) res.value;
		} catch (JFlexError e) {
			throw new XPathParserException("JFlex lexer error: " + e.reason());
		} catch (CupError e) {
			throw new XPathParserException("CUP parser error: " + e.reason());
		} catch (Exception e) {
			throw new XPathParserException(e.getMessage());
		}
	}
}
