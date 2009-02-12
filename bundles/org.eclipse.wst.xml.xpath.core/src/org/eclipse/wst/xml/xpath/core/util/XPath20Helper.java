/*******************************************************************************
 * Copyright (c) 2009 Standards for Technology in Automotive Retail and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     David Carver (STAR) - bug 226245 - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xml.xpath.core.util;

import javax.xml.xpath.XPathExpressionException;

import org.eclipse.wst.xml.xpath2.processor.JFlexCupParser;
import org.eclipse.wst.xml.xpath2.processor.XPathParser;
import org.eclipse.wst.xml.xpath2.processor.XPathParserException;

/**
 * @since 1.0
 */
public class XPath20Helper {

	public XPath20Helper() {
	}
	
	public static void compile(String xpathExp) throws XPathExpressionException {
		try {
			XPathParser xpathParser = new JFlexCupParser();
			xpathParser.parse(xpathExp);
		} catch (XPathParserException ex) {
			throw new XPathExpressionException(ex.getMessage());
		}
	}

}
